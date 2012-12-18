package com.ForgeEssentials.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.util.OutputHandler;

public class SQLDataDriver extends DataDriver
{
	
	public enum SQLType
	{
		INVALID("nil", "null"),
		MySQL("MySQL", "com.mysql.jdbc.Driver"),
		SQLite("SQLite", "org.sqlite.jdbc"),
		// Future DB systems...
		;
		
		public String Name;
		public String DriverClass;
		
		private SQLType(String a, String b)
		{
			this.Name = a;
			this.DriverClass = b;
		}
	}
	
	private Connection dbConnection;
	private SQLType dbType = SQLType.INVALID;
	
	// Default constructor is good enough for us.

	@Override
	public boolean parseConfigs(Configuration config, String worldName)
	{
		boolean isSuccess = true;
		String type;
		String connectionString = "";
		String jdbcClass;
		String username = "";
		String password = "";		
		
		Property prop;
		prop = config.get("Data.SQL", "dbType", "SQLite");
		prop.comment = "Type of the Database to connect to. Currently supports: SQLite, MySQL";
		type = prop.value.toLowerCase();
		
		if (type.equals(SQLType.SQLite.Name.toLowerCase()))
		{
			this.dbType = SQLType.SQLite;
			
			// Set up the SQLite connection.
			jdbcClass = "org.sqlite.JDBC";
			
			prop = config.get("Data.SQLite", "dataFile", "ForgeEssentials/sqlite.db");
			prop.comment = "Path to the SQLite database file (only use leading slashes for an absolute path)";
			String path = prop.value;	
			
			connectionString = "jdbc:sqlite:" + path;
		}
		else if (type.equals(SQLType.MySQL.Name.toLowerCase()))
		{
			this.dbType = SQLType.MySQL;
			jdbcClass = "com.mysql.jdbc.Driver";
			
			prop = config.get("Data.MySQL", "server", "localhost");
			prop.comment = "Server name/IP that hosts the database.";
			String mysqlServer = prop.value;
			
			prop = config.get("Data.MySQL", "port", 3306);
			prop.comment = "Port to connect to the database on";
			String mysqlPort = Integer.toString(prop.getInt());
			
			prop = config.get("Data.MySQL", "database", "ForgeEssentials");
			prop.comment = "Database name that FE will use to store its data & tables in. Highly reccomended to have a DB for FE data only.";
			String mysqlDatabase = prop.value;
			
			prop = config.get("Data.MySQL", "username", " ");
			prop.comment = "Username to log into DB with";
			username = prop.value;
			
			prop = config.get("Data.MySQL", "password", " ");
			prop.comment = "Password to log into DB with";
			password = prop.value;
			
			connectionString = "jdbc:mysql://" + mysqlServer + ":" + mysqlPort + "/" + mysqlDatabase;
		}
		else
		{
			OutputHandler.SOP("CONFIGURATION ERROR: Invalid ForgeEssentials config value! Data.SQL.dbType has an unsupported database type: " + type);
			OutputHandler.SOP("Please READ the config options and ensure you have selected a SUPPORTED database driver.");
		}

		if (this.dbType != SQLType.INVALID)
		{
			try
			{
				Class driverClass = Class.forName(this.dbType.DriverClass);
	
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
				OutputHandler.SOP("Could not load the JDBC Driver! (" + type + ") Does it exist in the lib directory?");
			}
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
	
	private void createTable(String tableName)
	{
		
	}
}
