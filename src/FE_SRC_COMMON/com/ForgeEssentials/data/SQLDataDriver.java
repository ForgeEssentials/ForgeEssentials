package com.ForgeEssentials.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.EnumDriverType;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.api.data.TypeEntryInfo;
import com.ForgeEssentials.api.data.TypeMultiValInfo;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.DBConnector;
import com.ForgeEssentials.util.EnumDBType;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.Pair;
import com.google.common.collect.HashMultimap;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

@SuppressWarnings("rawtypes")
public class SQLDataDriver extends AbstractDataDriver
{
	protected static final String	SEPERATOR			= "__";
	private Connection				dbConnection;
	private HashSet<String>			classTableChecked	= new HashSet<String>();
	protected DBConnector			connector;

	private final String			UNIQUE				= "uniqueIdentifier";
	private final String			MULTI_MARKER		= "MultiValUID";
	private final String			FEDATA_PREFIX		= "FEDATA_";

	private String					SURROUNDER			= "";

	// Default constructor is good enough for us.

	public SQLDataDriver()
	{
		connector = new DBConnector("CoreData", null, EnumDBType.H2_FILE, "ForgeEssentials", ForgeEssentials.FEDIR.getPath() + "/FEData", false);
	}

	@Override
	public void loadFromConfigs(Configuration config, String category) throws SQLException, ClassNotFoundException
	{
		String cat = category.substring(0, category.lastIndexOf('.'));

		connector.loadOrGenerate(config, cat);
	}

	/**
	 * To ensure that the connection stays connected when needed.
	 * @return
	 */
	public Connection getDbConnection()
	{
		try
		{
			if (dbConnection == null || dbConnection.isClosed())
			{
				dbConnection = connector.getChosenConnection();
			}
		}
		catch (Exception e)
		{
			OutputHandler.exception(Level.SEVERE, "DataDriver SQL conncetion could not be recreated!", e);
		}

		return dbConnection;
	}

	@Override
	public void serverStart(FMLServerStartingEvent e)
	{
		// actually start the connection.
		dbConnection = connector.getChosenConnection();
		if (connector.getActiveType().equals(EnumDBType.H2_FILE))
		{
			SURROUNDER = "\"";
		}
		else
		{
			SURROUNDER = "";
		}
	}

	@Override
	public void onClassRegistered(ITypeInfo tagger)
	{
		// If this is the first time registering a class that is NOT saved inline,
		// attempt to create a table.
		if (!(tagger.canSaveInline() || classTableChecked.contains(tagger.getType().getName())))
		{
			// automatically adds done classes to the set
			createTable(tagger.getType());
		}
	}

	@Override
	protected boolean saveData(ClassContainer type, TypeData data)
	{
		try
		{
			Statement s;
			ArrayList<String> statements = generateInsertBatch(DataStorageManager.getInfoForType(type), data);
			s = getDbConnection().createStatement();

			for (String statement : statements)
			{
				s.addBatch(statement);
			}

			s.executeBatch();
			return true;
		}
		catch (SQLException e)
		{
			OutputHandler.exception(Level.WARNING, "Couldn't save object of type " + type.getSimpleName() + " to " + connector.getActiveType() + " DB. Server will continue running.", e);
			return false;
		}
	}

	@Override
	protected TypeData loadData(ClassContainer type, String uniqueKey)
	{
		TypeData reconstructed = DataStorageManager.getDataForType(type);
		ITypeInfo info = DataStorageManager.getInfoForType(type);

		try
		{
			Statement s = getDbConnection().createStatement();
			ResultSet result = s.executeQuery(createSelectStatement(type, uniqueKey));

			// ResultSet initially sits just before first result.
			if (result.next())
			{
				// Should only be one item in this set.
				createTaggedClassFromResult(resultRowToMap(result), info, reconstructed);
			}
		}
		catch (SQLException e)
		{
			OutputHandler.exception(Level.FINE, "Couldn't load object of type " + type.getSimpleName() + " from " + connector.getActiveType() + " DB.", e);
			return null;
		}

		return reconstructed;
	}

	@Override
	protected TypeData[] loadAll(ClassContainer type)
	{
		ArrayList<TypeData> values = new ArrayList<TypeData>();
		ITypeInfo info = DataStorageManager.getInfoForType(type);

		try
		{
			Statement s = getDbConnection().createStatement();
			ResultSet result = s.executeQuery(createSelectAllStatement(type));
			TypeData temp;

			while (result.next())
			{
				temp = DataStorageManager.getDataForType(type);

				// Continue reading rows as they exist.
				createTaggedClassFromResult(resultRowToMap(result), info, temp);
				values.add(temp);
			}
		}
		catch (SQLException e)
		{
			OutputHandler.exception(Level.FINE, "Couldn't load objects of type " + type.getSimpleName() + " from " + connector.getActiveType() + " DB.", e);
		}

		return values.toArray(new TypeData[values.size()]);
	}

	@Override
	protected boolean deleteData(ClassContainer type, String uniqueObjectKey)
	{
		try
		{
			ArrayList<String> statements = createDeleteStatement(type, uniqueObjectKey);
			Statement s = getDbConnection().createStatement();

			for (String statement : statements)
			{
				s.addBatch(statement);
			}

			s.executeBatch();
		}
		catch (SQLException e)
		{
			OutputHandler.exception(Level.SEVERE, "Problem deleting data from " + connector.getActiveType() + " DB (May not actually be a critical error):", e);
			return false;
		}

		return true;
	}

	/**
	 * Transforms a ResultSet row into a HashMap. Assumes a valid result is
	 * @param result currently selected.
	 * @return
	 */
	private HashMap<String, Object> resultRowToMap(ResultSet result)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();

		try
		{
			// Determine column names
			ResultSetMetaData meta = result.getMetaData();
			ArrayList<String> names = new ArrayList<String>();

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

	private void createTaggedClassFromResult(HashMap<String, Object> result, ITypeInfo info, TypeData data) throws SQLException
	{
		ITypeInfo infoCursor;
		TypeData dataCursor = null;
		ClassContainer tmpClass;
		Object tempVal;

		for (Entry<String, Object> entry : result.entrySet())
		{
			dataCursor = data;
			infoCursor = info;

			String[] fieldHeiarchy = entry.getKey().split(SEPERATOR);
			if (fieldHeiarchy != null)
			{
				// Iterate over the list of items in the hierarchy to rebuild the
				// TaggedClass.
				for (int i = 0; i < fieldHeiarchy.length; ++i)
				{
					String name = fieldHeiarchy[i];

					// Grab the next item
					tmpClass = infoCursor.getTypeOfField(name);

					// if its not the last one.
					if (fieldHeiarchy.length > i + 1)
					{
						// An object lives here. Add a new taggedClass of this type.
						tempVal = dataCursor.getFieldValue(name);

						if (tempVal == null)
						{
							tempVal = DataStorageManager.getDataForType(tmpClass);
						}

						dataCursor.putField(name, tempVal);

						// change the cursor to the new object.
						dataCursor = (TypeData) tempVal;
						infoCursor = infoCursor.getInfoForField(name);
					}
					else
					{
						// account for multivals.
						if (name.contains(MULTI_MARKER))
						{
							name = name.replace("_" + MULTI_MARKER, "");
							// get tmpClass again with new name.
							tmpClass = infoCursor.getTypeOfField(name);
						}

						// Primitive type.
						tempVal = valueToField(tmpClass, entry.getValue());
						dataCursor.putField(name, tempVal);
					}
				}
			}
		}
	}

	private ArrayList<String> createDeleteStatement(ClassContainer type, String unique) throws SQLException
	{
		ArrayList<String> list = new ArrayList<String>();

		// normal class delete thing.
		String statement = "DELETE FROM " + FEDATA_PREFIX + type.getFileSafeName() + " WHERE " + UNIQUE + "='" + unique + "'";
		list.add(statement);

		ITypeInfo info = DataStorageManager.getInfoForType(type);
		TypeData data = DataStorageManager.getDataForType(type);

		ResultSet set = getDbConnection().createStatement().executeQuery(createSelectStatement(type, unique));
		if (set.next())
		{
			createTaggedClassFromResult(resultRowToMap(set), info, data);
		}

		// container to UIDs
		HashMultimap<ClassContainer, String> multiMap = HashMultimap.create();
		collectMultiVals(info, data, multiMap);

		// create delete things for it.

		boolean isFirst = false;
		for (ClassContainer key : multiMap.keySet())
		{
			statement = "DELETE FROM " + FEDATA_PREFIX + key.getFileSafeName() + " WHERE " + TypeMultiValInfo.UID + "='";
			isFirst = false;
			for (String valID : multiMap.get(key))
			{
				if (isFirst)
				{
					statement += valID + "'";
				}
				else
				{
					statement += " OR " + TypeMultiValInfo.UID + "='" + valID + "'";
				}
			}
			list.add(statement);
		}

		return list;
	}

	private void collectMultiVals(ITypeInfo info, TypeData data, HashMultimap<ClassContainer, String> map)
	{
		HashMultimap.create();

		ITypeInfo tempInfo;
		String id;
		for (Entry<String, Object> e : data.getAllFields())
		{
			info.getTypeOfField(e.getKey());
			tempInfo = info.getInfoForField(e.getKey());

			if (tempInfo == null)
			{
				continue;
			}
			else if (!tempInfo.canSaveInline())
			{
				id = e.getValue().toString();
				map.put(tempInfo.getType(), id);
			}
			else if (e.getValue() instanceof TypeData)
			{
				collectMultiVals(info, (TypeData) e.getValue(), map);
			}
		}
	}

	private String createSelectAllStatement(ClassContainer type)
	{
		return createSelectStatement(type, null);
	}

	private String createSelectStatement(ClassContainer type, String unique)
	{
		StringBuilder builder = new StringBuilder();

		// Basic SELECT syntax
		builder.append("SELECT * FROM " + FEDATA_PREFIX + type.getFileSafeName());
		// Conditional
		if (unique != null)
		{
			builder.append(" WHERE ");
			DataStorageManager.getInfoForType(type);
			builder.append(UNIQUE);
			builder.append("=");
			builder.append('\'').append(unique).append('\'');
		}

		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> generateInsertBatch(ITypeInfo info, TypeData data)
	{
		ClassContainer type = info.getType();

		ArrayList<Pair<String, String>> fieldValueMap = new ArrayList<Pair<String, String>>();
		ArrayList<String> statements = new ArrayList<String>();

		// MultiVal list
		ArrayList<Pair<String, TypeData>> multiVals = new ArrayList<Pair<String, TypeData>>();

		// Iterate through fields and build up name=>value pair list.
		ArrayList<Pair> temp;
		for (Entry<String, Object> entry : data.getAllFields())
		{
			temp = fieldToValues(entry.getKey(), info.getTypeOfField(entry.getKey()), entry.getValue());

			// catch multivals and add them to a different list.
			for (Pair p : temp)
			{
				if (((String) p.getFirst()).contains(MULTI_MARKER) && p.getSecond() instanceof TypeData)
				{
					multiVals.add(p);
				}
				else
				{
					fieldValueMap.add(p);
				}
			}
		}

		String table = type.getFileSafeName();
		boolean isEntry = false;

		// Deal with unique field. No uniqueFields for these...
		if (!(info instanceof TypeEntryInfo))
		{
			fieldValueMap.add(0, new Pair(UNIQUE, data.getUniqueKey()));
		}
		else
		{
			table = ((TypeEntryInfo) info).getParentType().getFileSafeName();
			isEntry = true;
		}

		table = FEDATA_PREFIX + table;

		String query = getInsertStatement(fieldValueMap, table, isEntry);
		statements.add(query.toString());

		ArrayList<String> tempStatements;
		for (Pair<String, TypeData> p : multiVals)
		{
			tempStatements = generateMultiValInsertBatch(p.getSecond());
			statements.addAll(tempStatements);
		}

		return statements;
	}

	private String getInsertStatement(ArrayList<Pair<String, String>> list, String table, boolean isEntry)
	{
		EnumDBType db = connector.getActiveType();

		StringBuilder query = new StringBuilder();

		if (isEntry)
		{
			query.append("INSERT INTO " + table + ' ');
		}
		else
		{
			switch (db)
				{
					case H2_FILE:
						query.append("MERGE INTO " + table + ' ');
						break;
					case MySQL:
						query.append("REPLACE INTO " + table + ' ');
						break;
				}
		}

		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		fields.append('(');
		values.append('(');

		Iterator<Pair<String, String>> itr = list.iterator();
		Pair<String, String> pair;
		boolean isfirst = true;
		while (itr.hasNext())
		{
			pair = itr.next();

			if (isfirst)
			{
				isfirst = !isfirst;
				fields.append(SURROUNDER + pair.getFirst() + SURROUNDER);
			}
			else
			{
				fields.append(", " + SURROUNDER + pair.getFirst() + SURROUNDER);
				values.append(", ");
			}

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

		// add statement to list.
		return query.toString();
	}

	private ArrayList<String> generateMultiValInsertBatch(TypeData data)
	{
		ArrayList<String> statements = new ArrayList<String>();

		TypeMultiValInfo info = (TypeMultiValInfo) DataStorageManager.getInfoForType(data.getContainer());
		ITypeInfo entryInfo = info.getEntryInfo();

		for (Object dat : data.getAllValues())
		{
			statements.addAll(generateInsertBatch(entryInfo, (TypeData) dat));
		}

		return statements;
	}

	/**
	 * Attempts to create a table to store the type passed to it. These should
	 * only be top-level types that need to be stored, such as PlayerInfo and
	 * Zones. Points, WorldPoints and other "simple" types that are contained
	 * within the top-level types will be unrolled automatically.
	 * @param type
	 * @return
	 */
	private boolean createTable(ClassContainer type)
	{
		if (classTableChecked.contains(type.getName()))
			return false;

		ITypeInfo tagger = DataStorageManager.getInfoForType(type);
		String[] fields = tagger.getFieldList();
		ArrayList<Pair<String, String>> tableFields = new ArrayList<Pair<String, String>>();
		String keyClause = "";

		boolean isMulti = tagger instanceof TypeMultiValInfo;

		// if its multi, add the UID thing.
		if (isMulti)
		{
			tableFields.add(new Pair<String, String>(MULTI_MARKER, "VARCHAR(100)"));

			TypeEntryInfo info = ((TypeMultiValInfo) tagger).getEntryInfo();

			for (String name : fields)
			{
				tableFields.addAll(fieldToColumns(info, name, name));
			}
		}
		else
		{
			for (String name : fields)
			{
				tableFields.addAll(fieldToColumns(tagger, name, name));
			}
		}

		// no saving the UniqueKey if its a MultiVal thing.
		if (!isMulti)
		{
			tableFields.add(new Pair<String, String>(UNIQUE, "VARCHAR(100)"));
			keyClause = "PRIMARY KEY (" + SURROUNDER + UNIQUE + SURROUNDER + ")";
		}

		// Build up the create statement
		StringBuilder tableCreate = new StringBuilder("CREATE TABLE IF NOT EXISTS " + FEDATA_PREFIX + type.getFileSafeName() + " (");
		for (Pair<String, String> pair : tableFields)
		{
			tableCreate.append(SURROUNDER + pair.getFirst() + SURROUNDER + " " + pair.getSecond() + ", ");
		}

		if (isMulti)
		{
			tableCreate.replace(tableCreate.lastIndexOf(","), tableCreate.length(), ")");
		}
		else
		{
			// Add primary key clause.
			tableCreate.append(keyClause + ")");
		}

		try
		{
			// Attempt to execute the statement.
			Statement s = getDbConnection().createStatement();
			s.execute(tableCreate.toString());
		}
		catch (Exception e)
		{
			OutputHandler.exception(Level.SEVERE, "Failed to create table for " + type.getName(), e);
			e.printStackTrace();
			return false;
		}

		classTableChecked.add(type.getName());

		return true;
	}

	/**
	 * Examines the provided type and produces an array of field => H2 Type
	 * pairs, ideal for creating new tables with. Complex type fields are broken
	 * down into constituent primitives in the form of:
	 * "parentField_childFieldName"
	 * @param columnName Name of saved field
	 * @param type Type of saved field
	 * @return Array of field => H2 type names.
	 */
	private ArrayList<Pair<String, String>> fieldToColumns(ITypeInfo info, String columnName, String field)
	{
		ArrayList<Pair<String, String>> fields = new ArrayList<Pair<String, String>>();
		ClassContainer con = info.getTypeOfField(field);
		Class type = con.getType();

		if (!StorageManager.isTypeComplex(con))
		{
			if (type.equals(int.class) || type.equals(Integer.class))
			{
				fields.add(new Pair<String, String>(columnName, "INTEGER"));
			}
			else if (type.equals(byte.class) || type.equals(Byte.class))
			{
				fields.add(new Pair<String, String>(columnName, "TINYINT"));
			}
			else if (type.equals(boolean.class) || type.equals(Boolean.class))
			{
				fields.add(new Pair<String, String>(columnName, "BOOLEAN"));
			}
			else if (type.equals(double.class) || type.equals(Double.class))
			{
				fields.add(new Pair<String, String>(columnName, "DOUBLE"));
			}
			else if (type.equals(long.class) || type.equals(Long.class))
			{
				fields.add(new Pair<String, String>(columnName, "BIGINT"));
			}
			else if (type.equals(float.class) || type.equals(Float.class))
			{
				fields.add(new Pair<String, String>(columnName, "FLOAT"));
			}
			else if (type.equals(String.class))
			{
				fields.add(new Pair<String, String>(columnName, "VARCHAR(255)"));
			}
			else if (type.equals(double[].class) || type.equals(int[].class) || type.equals(boolean[].class) || type.equals(String[].class) || type.equals(byte[].class) || type.equals(float[].class) || type.equals(long[].class))
			{
				// We are going to roll arrays up into arbitrary long text
				// fields.
				fields.add(new Pair<String, String>(columnName, "TEXT"));
			}
			else
			{
				// Unsupported. This will probably be crazy.
				fields.add(new Pair<String, String>(columnName, "BLOB"));
			}
		}
		else
		{
			// Complex type we can't handle.
			ITypeInfo tagger = info.getInfoForField(field);

			if (tagger instanceof TypeMultiValInfo || !tagger.canSaveInline())
			{
				// special stuff for Multivals. this will be a key going to a different table.
				createTable(con);
				fields.add(new Pair<String, String>(columnName + "_" + MULTI_MARKER, "VARCHAR(255)"));
			}
			else
			{
				// some other complex type.
				String[] fieldList = tagger.getFieldList();

				// Iterate over the stored fields. Recurse if nessecary.
				for (String name : fieldList)
				{
					fields.addAll(fieldToColumns(tagger, columnName + SEPERATOR + name, name));
				}

			}
		}

		return fields;
	}

	/**
	 * Generates an array of fieldname => String(Value) pairs, useful for
	 * Inserts, Updates, or Deletes.
	 * @param fieldName Name of the field in the H2 DB
	 * @param cType Type of field (Java)
	 * @param value
	 * @return Array of fieldname => value pairs
	 */
	@SuppressWarnings({ "unchecked" })
	private ArrayList<Pair> fieldToValues(String fieldName, ClassContainer type, Object value)
	{
		ArrayList<Pair> data = new ArrayList<Pair>();

		Class cType = type.getType();

		if (cType.equals(Integer.class) || cType.equals(Float.class) || cType.equals(Double.class) || cType.equals(Long.class) || cType.equals(String.class)
				|| cType.equals(int.class) || cType.equals(float.class) || cType.equals(double.class) || cType.equals(long.class))
		{
			data.add(new Pair(fieldName, value.toString()));
		}
		else if (cType.equals(Boolean.class) || cType.equals(boolean.class))
		{
			data.add(new Pair(fieldName, "" + (Boolean.TRUE.equals(value) ? 1 : 0)));
		}
		else if (cType.equals(int[].class) && ((int[]) value).length > 0)
		{
			int[] arr = (int[]) value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append("'").append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.append("'").toString()));
		}
		else if (cType.equals(float[].class) && ((float[]) value).length > 0)
		{
			float[] arr = (float[]) value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append("'").append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.append("'").toString()));
		}
		else if (cType.equals(double[].class) && ((double[]) value).length > 0)
		{
			double[] arr = (double[]) value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append("'").append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.append("'").toString()));
		}
		else if (cType.equals(long[].class) && ((long[]) value).length > 0)
		{
			long[] arr = (long[]) value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append("'").append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.append("'").toString()));
		}
		else if (cType.equals(boolean[].class) && ((boolean[]) value).length > 0)
		{
			boolean[] arr = (boolean[]) value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append("'").append(String.valueOf(arr[0]));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("," + String.valueOf(arr[i]));
			}
			data.add(new Pair(fieldName, tempStr.append("'").toString()));
		}
		else if (cType.equals(String[].class) && ((String[]) value).length > 0)
		{
			String[] arr = (String[]) value;
			StringBuilder tempStr = new StringBuilder();
			tempStr.append("'").append(String.valueOf(arr[0]).replace("'", "\"\""));
			for (int i = 1; i < arr.length; ++i)
			{
				tempStr.append("!??!" + String.valueOf(arr[i]).replace("'", "\"\""));
			}
			data.add(new Pair(fieldName, tempStr.append("'").toString()));
		}
		else if (value.getClass().equals(TypeData.class))
		{
			// Tricky business involving recursion.
			TypeData tc = (TypeData) value;
			ITypeInfo info = DataStorageManager.getInfoForType(type);

			if (info instanceof TypeMultiValInfo || !info.canSaveInline())
			{
				// special stuff for Multivals. this will be a key going to a different table.
				data.add(new Pair(fieldName + "_" + MULTI_MARKER, tc.getUniqueKey()));

				// this will be removed what all the MultiVal ones are collected.
				data.add(new Pair(fieldName + "_" + MULTI_MARKER, tc));
			}
			else
			{
				for (Entry<String, Object> f : tc.getAllFields())
				{
					data.addAll(fieldToValues(fieldName + SEPERATOR + f.getKey(), info.getTypeOfField(f.getKey()), f.getValue()));
				}
			}
		}
		else
		// What the fuck? This will be unpredictable.
		{
			data.add(new Pair(fieldName, value.toString()));
		}
		return data;
	}

	// Transforms the raw DB type back into a Java object.
	private Object valueToField(ClassContainer targetType, Object dbValue) throws SQLException
	{
		Object value = null;

		if (targetType == null)
			return null;

		Class type = targetType.getType();

		if (type.equals(int.class) || type.equals(float.class) || type.equals(double.class) || type.equals(long.class) || type.equals(String.class) || type.equals(boolean.class))
		{
			// DB Value is an integer
			value = dbValue;
		}
		else if (type.equals(byte.class))
		{
			// DB Value is an Integer
			value = ((Integer) dbValue).byteValue();
		}
		else if (type.equals(double[].class))
		{
			// DB value is a string representing an array of doubles, separated
			// by ','
			String[] values = ((String) dbValue).split(",");
			double[] result = new double[values.length];

			for (int i = 0; i < values.length; ++i)
			{
				result[i] = Double.valueOf(values[i]).doubleValue();
			}
			value = result;
		}
		else if (type.equals(int[].class))
		{
			// DB value is a string representing an array of integers, separated
			// by ','
			String[] values = ((String) dbValue).split(",");
			int[] result = new int[values.length];

			for (int i = 0; i < values.length; ++i)
			{
				result[i] = Integer.valueOf(values[i]).intValue();
			}
			value = result;
		}
		else if (type.equals(byte[].class))
		{
			// DB value is a string representing an array of integers, separated
			// by ','
			String[] values = ((String) dbValue).split(",");
			byte[] result = new byte[values.length];

			for (int i = 0; i < values.length; ++i)
			{
				result[i] = Byte.valueOf(values[i]).byteValue();
			}
			value = result;
		}
		else if (type.equals(boolean[].class))
		{
			// DB value is a string representing an array of booleans, separated
			// by ','
			String[] values = ((String) dbValue).split(",");
			boolean[] result = new boolean[values.length];

			for (int i = 0; i < values.length; ++i)
			{
				result[i] = Boolean.valueOf(values[i]).booleanValue();
			}
			value = result;
		}
		else if (type.equals(String[].class))
		{
			// DB value is a string representing an array of strings, separated
			// by '!??!'
			// Each item may contain instances of '""', which represents a
			// single apostrophe.
			String[] values = ((String) dbValue).split("!??!");

			for (int i = 0; i < values.length; ++i)
			{
				values[i] = values[i].replaceAll("\"\"", "'");
			}
			value = values;
		}
		else
		{
			// for small things like this...
			ITypeInfo info = DataStorageManager.getInfoForType(targetType);

			// multival styff
			if (!info.canSaveInline())
			{
				TypeMultiValInfo multiInfo = (TypeMultiValInfo) info;
				String ID = dbValue.toString();
				ID = TypeMultiValInfo.getUIDFromUnique(ID);

				// get the data for the MultiVals
				Statement s = getDbConnection().createStatement();
				ResultSet result = s.executeQuery("SELECT * FROM " + FEDATA_PREFIX + targetType.getFileSafeName() + " WHERE " + MULTI_MARKER + "='" + ID + "'");

				TypeData data = DataStorageManager.getDataForType(targetType);

				TypeEntryInfo entryInfo = multiInfo.getEntryInfo();
				String connector = multiInfo.getEntryName();

				// create the MultiVal object
				TypeData temp;
				int i = 0;
				while (result.next())
				{
					temp = DataStorageManager.getDataForType(info.getType());
					createTaggedClassFromResult(resultRowToMap(result), entryInfo, data);
					data.putField(connector + i++, temp);
				}

				// return the compelted MultiVal object
				value = data;
			}

			// anything else?
		}
		return value;
	}

	@Override
	public EnumDriverType getType()
	{
		return EnumDriverType.SQL;
	}
}
