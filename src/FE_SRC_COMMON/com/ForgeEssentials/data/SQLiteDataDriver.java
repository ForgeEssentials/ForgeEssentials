package com.ForgeEssentials.data;

import java.sql.Connection;
import java.sql.DriverManager;
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

public class SQLiteDataDriver extends DataDriver
{
	private String DriverClass = "org.sqlite.JDBC";
	private Connection dbConnection;
	private HashMap<Class, Boolean> classTableChecked = new HashMap<Class, Boolean>();
	
	// Default constructor is good enough for us.

	@Override
	public boolean parseConfigs(Configuration config, String worldName)
	{
		boolean isSuccess = true;
		String type;

		// Set up the SQLite connection.
		Property prop = config.get("Data.SQLite", "dataFile", "ForgeEssentials/sqlite.db");
		prop.comment = "Path to the SQLite database file (only use leading slashes for an absolute path)";
		String path = prop.value;
		
		// Save any additional categories we may have created.
		config.save();

		try
		{
			Class driverClass = Class.forName(DriverClass);

			this.dbConnection = DriverManager.getConnection("jdbc:sqlite:" + path);
			
			isSuccess = true;
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Unable to connect to the database!");
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			OutputHandler.SOP("Could not load the SQLite JDBC Driver! Does it exist in the lib directory?");
		}
		
		return isSuccess;
	}

	@Override
	public void registerClass(Class type)
	{
		super.registerClass(type);

		SaveableObject annotation;
		if ((annotation = (SaveableObject)type.getAnnotation(SaveableObject.class)) != null)
		{
			// If this is the first time registering a class that is NOT saved inline,
			//  attempt to create a table.
			if (!(annotation.SaveInline() || this.classTableChecked.containsKey(type)))
			{
				this.createTable(type);
			}
		}
	}

	@Override
	protected boolean saveData(Class type, TaggedClass fieldList)
	{
		boolean isSuccess = false;

		return isSuccess;
	}

	@Override
	protected TaggedClass loadData(Class type, Object uniqueKey)
	{
		TaggedClass reconstructed = null;
		
		return reconstructed;
	}

	@Override
	protected TaggedClass[] loadAll(Class type)
	{
		TaggedClass[] value = null;

		ArrayList<TaggedClass> values = new ArrayList<TaggedClass>();
		
		value = values.toArray(new TaggedClass[values.size()]);

		return value;
	}

	@Override
	protected boolean deleteData(Class type, Object uniqueObjectKey)
	{
		boolean isSuccess = false;

		return isSuccess;
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
		
		TypeTagger tagger = this.getTaggerForType(type);
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
				fields.add(new Pair<String, String>(fieldName, "INTEGER"));
			}
			else if (type.equals(float.class) || type.equals(Float.class) ||
					type.equals(double.class) || type.equals(Double.class))
			{
				fields.add(new Pair<String, String>(fieldName, "REAL"));
			}
			else if (type.equals(String.class) || type.equals(double[].class) ||
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
			TypeTagger tagger = this.getTaggerForType(type);
			Iterator<Entry<String, Class>> iterator = tagger.fieldToTypeMap.entrySet().iterator();
			
			// Iterate over the stored fields. Recurse if nessecary.
			while (iterator.hasNext())
			{
				Entry<String, Class> entry = iterator.next();
				fields.addAll(this.fieldToColumns(fieldName + "_" + entry.getKey(), entry.getValue()));
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
			tempStr.append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.toString()));
		}
		else if (type.equals(int[].class) && ((int[])value).length > 0)
		{
			int[] arr = (int[])value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.toString()));		
		}
		else if (type.equals(boolean[].class) && ((boolean[])value).length > 0)
		{
			boolean[] arr = (boolean[])value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.toString()));
		}
		else if (type.equals(String[].class) && ((String[])value).length > 0)
		{
			String[] arr = (String[])value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("!??!" + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.toString()));			
		}
		else if (type.equals(TaggedClass.class))
		{
			// Tricky business involving recursion.
			TaggedClass tc = (TaggedClass)value;
			
			for (SavedField f : tc.TaggedMembers.values())
			{
				data.addAll(this.fieldToValues(fieldName + "_" + f.FieldName, f.Type, f.Value));
			}
		}
		else // What the fuck? This will be unpredictable.
		{
			data.add(new Pair(fieldName, value.toString()));
		}
		return data;
	}
}
