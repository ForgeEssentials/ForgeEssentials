package com.ForgeEssentials.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.data.TaggedClass.SavedField;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.Pair;

public class MySQLDataDriver extends DataDriver
{
	private static String separationString = "__";
	private String DriverClass = "com.mysql.jdbc.Driver";
	private Connection dbConnection;
	private HashMap<Class, Boolean> classTableChecked = new HashMap<Class, Boolean>();
	
	// Default constructor is good enough for us.

	@Override
	public void parseConfigs(Configuration config, String worldName) throws SQLException, ClassNotFoundException
	{
		String type;
		String connectionString = "";

		// Set up the MySQL connection.
		Property prop;
		prop = config.get("Data.SQL", "server", "server.example.com");
		prop.comment = "Server name/IP that hosts the database.";
		String server = prop.value;

		prop = config.get("Data.SQL", "port", 3306);
		prop.comment = "Port to connect to the database on";
		String port = Integer.toString(prop.getInt());

		prop = config.get("Data.SQL", "database", "ForgeEssentials");
		prop.comment = "Database name that FE will use to store its data & tables in. Highly reccomended to have a DB for FE data only.";
		String database = prop.value;

		prop = config.get("Data.SQL", "username", " ");
		prop.comment = "Username to log into DB with";
		String username = prop.value;

		prop = config.get("Data.SQL", "password", " ");
		prop.comment = "Password to log into DB with";
		String password = prop.value;
		
		
		if (!server.equalsIgnoreCase("server.example.com"))
		{
			connectionString = "jdbc:mysql://" + server + ":" + port + "/" + database;;
	
			try
			{
				Class driverClass = Class.forName(DriverClass);
	
				this.dbConnection = DriverManager.getConnection(connectionString, username, password);
			}
			catch (SQLException e)
			{
				OutputHandler.SOP("Unable to connect to the database. Check your connection info.");
				throw e;
			}
			catch (ClassNotFoundException e)
			{
				OutputHandler.SOP("Could not load the MySQL JDBC Driver! Does it exist in the lib directory?");
				throw e;
			}
		}
	}


	@Override
	public void onClassRegisterred(TypeTagger tagger)
	{
		// If this is the first time registering a class that is NOT saved inline,
		//  attempt to create a table.
		if (!(tagger.inLine || this.classTableChecked.containsKey(tagger.forType)))
			this.createTable(tagger.forType);
	}

	@Override
	protected boolean saveData(Class type, TaggedClass fieldList)
	{
		boolean isSuccess = false;

		try
		{
			Statement s;
			s = this.dbConnection.createStatement();
			int count = s.executeUpdate(createInsertStatement(type, fieldList));
			
			isSuccess = true;
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Couldn't save object of type " + type.getSimpleName() + " to MySQL DB. Server will continue running.");
			e.printStackTrace();
		}

		return isSuccess;
	}

	@Override
	protected TaggedClass loadData(Class type, Object uniqueKey)
	{
		TaggedClass reconstructed = null;
		
		try
		{
			Statement s = this.dbConnection.createStatement();
			ResultSet result = s.executeQuery(this.createSelectStatement(type, uniqueKey));
			
			// ResultSet initially sits just before first result.
			if (result.next())
			{
				// Should only be one item in this set.
				reconstructed = this.createTaggedClassFromResult(type, this.resultRowToMap(result));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return reconstructed;
	}

	@Override
	protected TaggedClass[] loadAll(Class type)
	{
		ArrayList<TaggedClass> values = new ArrayList<TaggedClass>();
		
		try
		{
			Statement s = this.dbConnection.createStatement();
			ResultSet result = s.executeQuery(this.createSelectAllStatement(type));
			
			while (result.next())
			{
				// Continue reading rows as they exist.
				values.add(this.createTaggedClassFromResult(type, this.resultRowToMap(result)));
			}			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return values.toArray(new TaggedClass[values.size()]);
	}

	@Override
	protected boolean deleteData(Class type, Object uniqueObjectKey)
	{
		boolean isSuccess = false;

		try
		{
			Statement s = this.dbConnection.createStatement();
			s.execute(this.createDeleteStatement(type, uniqueObjectKey));
			
			isSuccess = true;
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Problem deleting data from MySQL DB (May not actually be a critical error):");
			e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	// Transforms a ResultSet row into a HashMap. Assumes a valid result is currently selected.
	private HashMap<String, Object> resultRowToMap(ResultSet result)
	{
		HashMap<String, Object> map = new HashMap();
		
		try
		{
			// Determine column names
			ResultSetMetaData meta = result.getMetaData();
			ArrayList<String> names = new ArrayList();
			
			// ResultSet columns start at 1. (Crazy, right?)
			for (int i = 1; i < meta.getColumnCount(); ++i)
			{
				names.add(meta.getColumnName(i));
			}
			
			// Pull values into map.
			Object val = null;
			for (String name : names)
			{
				val = result.getObject(name);
				if (val != null)
				{
					// Only add something to the map if it has a value.
					map.put(name, val);
				}
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}		
		
		return map;
	}

	private TaggedClass createTaggedClassFromResult(Class type, HashMap<String, Object> result)
	{
		TypeTagger rootTagger = DataStorageManager.getTaggerForType(type);
		TypeTagger taggerCursor;
		TaggedClass value = new TaggedClass();
		value.type = type;
		TaggedClass cursor = null;
		SavedField tmpField = null;
		
		for (Entry<String, Object> entry : result.entrySet())
		{
			cursor = value;
			taggerCursor = rootTagger;
			
			String[] fieldHeiarchy = entry.getKey().split(separationString);
			if (fieldHeiarchy != null)
			{
				// Iterate over the list of items in the heiarchy to rebuild the TaggedClass.
				for (int i = 0; i < fieldHeiarchy.length; ++i)
				{
					// Grab the next item
					tmpField = cursor.TaggedMembers.get(fieldHeiarchy[i]);
					
					if (tmpField == null)
					{
						// Create a new node for this position.
						tmpField = cursor.new SavedField();
						tmpField.name = fieldHeiarchy[i];
						cursor.addField(tmpField);
					}
					
					if (fieldHeiarchy.length > i + 1)
					{
						// An object lives here.
						tmpField.value = cursor = new TaggedClass();
						tmpField.type = taggerCursor.getTypeOfField(fieldHeiarchy[i]);
						taggerCursor = DataStorageManager.getTaggerForType(tmpField.type);
					}
					else
					{
						// Primitive type.
						Class fieldType = taggerCursor.getTypeOfField(fieldHeiarchy[i]);
						tmpField.value = this.valueToField(taggerCursor.getTypeOfField(fieldHeiarchy[i]), result.get(fieldHeiarchy[i]));
						tmpField.type = fieldType;
					}
				}
			}
		}
		
		return value;
	}
	
	private String createDeleteStatement(Class type, Object uniqueObjectKey)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("DELETE FROM " + type.getSimpleName() + " WHERE ");
		TypeTagger tagger = DataStorageManager.getTaggerForType(type);
		if (tagger.isUniqueKeyField)
		{
			builder.append(tagger.uniqueKey + " = ");
		}
		else
		{
			builder.append("uniqueIdentifier = ");
		}
		builder.append(uniqueObjectKey.toString());
		
		return builder.toString();
	}
	
	private String createSelectAllStatement(Class type)
	{
		return this.createSelectStatement(type, null);
	}

	private String createSelectStatement(Class type, Object uniqueObjectKey)
	{
		StringBuilder builder = new StringBuilder();
		
		// Basic SELECT syntax
		builder.append("SELECT * FROM " + type.getSimpleName());
		// Conditional
		if (uniqueObjectKey != null)
		{
			builder.append(" WHERE ");
			TypeTagger tagger = DataStorageManager.getTaggerForType(type);
			if (tagger.isUniqueKeyField)
			{
				builder.append(tagger.uniqueKey);
			}
			else
			{
				builder.append("uniqueIdentifier");
			}
			builder.append(" = ");
			
			if (uniqueObjectKey instanceof String)
			{
				builder.append("'").append((String)uniqueObjectKey).append("'");
			}
			else
			{
				builder.append(uniqueObjectKey.toString());
			}
		}
		
		return builder.toString();
	}
	
	private String createInsertStatement(Class type, TaggedClass fieldList)
	{
		ArrayList<Pair<String, String>> fieldValueMap = new ArrayList<Pair<String, String>>();
		// Iterate through fields and build up name=>value pair list.
		for (SavedField field : fieldList.TaggedMembers.values())
		{
			fieldValueMap.addAll(this.fieldToValues(field.name, field.type, field.value));
		}
		
		// Build up update statement.
		StringBuilder query = new StringBuilder();
		query.append("INSERT OR REPLACE INTO " + type.getSimpleName() + ' ');
		
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		fields.append('(');
		values.append('(');
		
		// Deal with unique field
		TypeTagger tagger = DataStorageManager.getTaggerForType(type);
		if (tagger.isUniqueKeyField)
		{
			fields.append(fieldList.uniqueKey.name);
		}
		else
		{
			fields.append("uniqueIdentifier");
		}
		if (fieldList.uniqueKey.type.equals(String.class))
		{
			values.append("'" + fieldList.uniqueKey.value + "'");
		}
		else
		{
			values.append(fieldList.uniqueKey.value);
		}
		
		Iterator<Pair<String, String>> itr = fieldValueMap.iterator();
		Pair<String, String> pair;
		while (itr.hasNext())
		{
			pair = itr.next();
			fields.append(", " + pair.getFirst());
			values.append(", ");
			if (pair.getSecond().getClass().equals(String.class))
			{
				values.append("'" + pair.getSecond() + "'");
			}
			else
			{
				values.append(pair.getSecond());
			}
		}
		fields.append(')');
		values.append(')');
		
		query.append(fields.toString() + " VALUES " + values.toString());
		
		return query.toString();
	}

	/**
	 * Attempts to create a table to store the type passed to it. These should only be top-level types that
	 * need to be stored, such as PlayerInfo and Zones. Points, WorldPoints and other "simple" types that are
	 * contained within the top-level types will be unrolled automatically.
	 * @param type
	 * @return
	 */
	private boolean createTable(Class type)
	{
		boolean isSuccess = false;
		
		TypeTagger tagger = DataStorageManager.getTaggerForType(type);
		HashMap<String, Class> fields = tagger.getFieldToTypeMap();
		ArrayList<Pair<String, String>> tableFields = new ArrayList<Pair<String, String>>();
		String keyClause = null;
		
		for (Entry<String, Class> entry : fields.entrySet())
		{
			tableFields.addAll(this.fieldToColumns(entry.getKey(), entry.getValue()));
		}

		if (tagger.isUniqueKeyField)
		{
			keyClause = "PRIMARY KEY (" + tagger.uniqueKey + ")";
		}
		else
		{
			// Is a method. Extra field required.
			tableFields.add(new Pair<String, String>("uniqueIdentifier", "TEXT"));
			keyClause = "PRIMARY KEY (uniqueIdentifier)";
		}
		
		// Build up the create statement
		StringBuilder tableCreate = new StringBuilder("CREATE TABLE IF NOT EXISTS " + type.getSimpleName() + " (");
		for (Pair<String, String> pair : tableFields)
		{
			tableCreate.append(pair.getFirst() + " " + pair.getSecond() + ", ");
		}
		// Add primary key clause.
		tableCreate.append(keyClause + ")");
		
		try
		{
			// Attempt to execute the statement.
			Statement s = this.dbConnection.createStatement();
			s.execute(tableCreate.toString());
			
			isSuccess = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return isSuccess;
	}
	
	/**
	 * Examines the provided type and produces an array of field => SQLite Type pairs,
	 * ideal for creating new tables with. Complex type fields are broken down into
	 * constituent primitives in the form of: "parentField_childFieldName"
	 * 
	 * @param fieldName Name of saved field
	 * @param type Type of saved field
	 * @return Array of field => SQLite type names.
	 */
	private ArrayList<Pair<String, String>> fieldToColumns(String fieldName, Class type)
	{
		ArrayList<Pair<String, String>> fields = new ArrayList<Pair<String, String>>();
		
		if (!TypeTagger.isTypeComplex(type))
		{
			if (type.equals(int.class) || type.equals(Integer.class) ||
				type.equals(boolean.class) || type.equals(Boolean.class))
			{
				fields.add(new Pair<String, String>(fieldName, "INT"));
			}
			else if (type.equals(float.class) || type.equals(Float.class) ||
					type.equals(double.class) || type.equals(Double.class))
			{
				fields.add(new Pair<String, String>(fieldName, "DOUBLE"));
			}
			else if (type.equals(String.class))
			{
				fields.add(new Pair<String, String>(fieldName, "VARCHAR(700)"));
			}
			else if (type.equals(double[].class) ||
					type.equals(int[].class) || type.equals(boolean[].class) ||
					type.equals(String[].class))
			{
				// We are going to roll arrays up into arbitrary long text fields.
				fields.add(new Pair<String, String>(fieldName, "TEXT"));
			}
			else
			{
				// Unsupported. This will probably be crazy.
				fields.add(new Pair<String, String>(fieldName, "BLOB"));
			}
		}
		else
		{
			// Complex type we can't handle.
			TypeTagger tagger = DataStorageManager.getTaggerForType(type);
			Iterator<Entry<String, Class>> iterator = tagger.fieldToTypeMap.entrySet().iterator();
		
			// Iterate over the stored fields. Recurse if nessecary.
			while (iterator.hasNext())
			{
				Entry<String, Class> entry = iterator.next();
				fields.addAll(this.fieldToColumns(fieldName + separationString + entry.getKey(), entry.getValue()));
			}
		}
	
		return fields;
	}
	
	/**
	 * Generates an array of fieldname => String(Value) pairs, useful for Inserts, Updates, or Deletes.
	 * 
	 * @param fieldName Name of the field in the SQLite DB
	 * @param type Type of field (Java)
	 * @param value
	 * @return Array of fieldname => value pairs
	 */
	private ArrayList<Pair<String, String>> fieldToValues(String fieldName, Class type, Object value)
	{
		ArrayList<Pair<String, String>> data = new ArrayList<Pair<String, String>>();
		
		if (type.equals(Integer.class) || type.equals(Boolean.class) || type.equals(Float.class) ||
				type.equals(Double.class) ||type.equals(String.class))
		{
			data.add(new Pair(fieldName, value.toString()));
		}
		else if (type.equals(double[].class) && ((double[])value).length > 0)
		{
			double[] arr = (double[])value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append("'").append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.append("'").toString()));
		}
		else if (type.equals(int[].class) && ((int[])value).length > 0)
		{
			int[] arr = (int[])value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append("'").append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.append("'").toString()));		
		}
		else if (type.equals(boolean[].class) && ((boolean[])value).length > 0)
		{
			boolean[] arr = (boolean[])value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append("'").append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.append("'").toString()));
		}
		else if (type.equals(String[].class) && ((String[])value).length > 0)
		{
			String[] arr = (String[])value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append("'").append(String.valueOf(arr[0]).replace("'", "\"\""));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("!??!" + String.valueOf(arr[i]).replace("'", "\"\""));
			}
			data.add(new Pair(fieldName, tempStr.append("'").toString()));			
		}
		else if (type.equals(TaggedClass.class))
		{
			// Tricky business involving recursion.
			TaggedClass tc = (TaggedClass)value;
			
			for (SavedField f : tc.TaggedMembers.values())
			{
				data.addAll(this.fieldToValues(fieldName + separationString + f.name, f.type, f.value));
			}
		}
		else // What the fuck? This will be unpredictable.
		{
			data.add(new Pair(fieldName, value.toString()));
		}
		return data;
	}
	
	// Transforms the raw DB type back into a Java object.
	private Object valueToField(Class targetType, Object dbValue)
	{
		Object value = null;
		if (targetType.equals(Integer.class))
		{
			// DB Value is an integer
			value = (Integer)dbValue;
		}
		else if (targetType.equals(Double.class))
		{
			// DB Value is a double
			value = (Double)dbValue;
		}
		else if (targetType.equals(Float.class))
		{
			// DB value is a Double.
			value = (Float)((Double)dbValue).floatValue();
		}
		else if (targetType.equals(String.class))
		{
			// DB Value is a string
			value = (String)dbValue;
		}
		else if (targetType.equals(Boolean.class))
		{
			// DB Value is an integer (1=true, 0=false)
			value = ((Integer)dbValue).equals(1);
		}
		else if (targetType.equals(double[].class))
		{
			// DB value is a string representing an array of doubles, separated by ','
			String[] values = ((String)dbValue).split(",");
			double[] result = new double[values.length]; 
			
			for (int i = 0; i < values.length; ++i)
			{
				result[i] = Double.valueOf(values[i]).doubleValue();
			}
			value = result;
		}
		else if (targetType.equals(int[].class))
		{
			// DB value is a string representing an array of integers, separated by ','
			String[] values = ((String)dbValue).split(",");
			int[] result = new int[values.length]; 
			
			for (int i = 0; i < values.length; ++i)
			{
				result[i] = Integer.valueOf(values[i]).intValue();
			}
			value = result;	
		}
		else if (targetType.equals(boolean[].class))
		{
			// DB value is a string representing an array of booleans, separated by ','
			String[] values = ((String)dbValue).split(",");
			boolean[] result = new boolean[values.length]; 
			
			for (int i = 0; i < values.length; ++i)
			{
				result[i] = Boolean.valueOf(values[i]).booleanValue();
			}
			value = result;
		}
		else if (targetType.equals(String[].class))
		{
			// DB value is a string representing an array of strings, separated by '!??!'
			// Each item may contain instances of '""', which represents a single apostrophe.
			String[] values = ((String)dbValue).split("!??!");
			
			for (int i = 0; i < values.length; ++i)
			{
				values[i] = values[i].replaceAll("\"\"", "'");
			}
			value = values;		
		}
		return value;
	}
}
