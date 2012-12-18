package com.ForgeEssentials.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.util.OutputHandler;

public class MySQLDataDriver extends DataDriver
{
	private String DriverClass = "org.sqlite.jdbc";
	private Connection dbConnection;
	
	// Default constructor is good enough for us.

	@Override
	public boolean parseConfigs(Configuration config, String worldName)
	{
		boolean isSuccess = true;
		String type;
		String connectionString = "";

		// Set up the MySQL connection.
		Property prop;
		prop = config.get("Data.SQL", "server", "localhost");
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
		
		connectionString = "jdbc:mysql://" + server + ":" + port + "/" + database;;

		try
		{
			Class driverClass = Class.forName(DriverClass);

			this.dbConnection = DriverManager.getConnection(connectionString, username, password);
			
			isSuccess = true;
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Unable to connect to the database!");
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			OutputHandler.SOP("Could not load the MySQL JDBC Driver! Does it exist in the lib directory?");
		}
		
		return isSuccess;
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
		ArrayList<TaggedClass> values = new ArrayList<TaggedClass>();
		
		return values.toArray(new TaggedClass[values.size()]);
	}

	@Override
	protected boolean deleteData(Class type, Object uniqueObjectKey)
	{
		boolean isSuccess = false;
		return isSuccess;
	}
	
	private void ensureTableExists(Class type)
	{
		TypeTagger tagger = this.getTaggerForType(type);
	}
	
	private void createTable(Class type)
	{
		TypeTagger tagger = this.getTaggerForType(type);
		HashMap<String, Class> fields = tagger.getFieldToTypeMap();
		
		Iterator<Entry<String, Class>> iterator = fields.entrySet().iterator();
		
		while (iterator.hasNext())
		{
			Entry<String, Class> entry = iterator.next();
			if (entry.getKey() == tagger.uniqueKey)
			{
				
			}
		}
	}
}
