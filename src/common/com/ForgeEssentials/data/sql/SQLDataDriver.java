package com.ForgeEssentials.data.sql;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.util.OutputHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDataDriver extends DataDriver
{
	
	public enum SQLType
	{
		MySQL("MySQL", "com.mysql.jdbc");
		// Future DB systems...
		
		public String Name;
		public String Package;
		
		private SQLType(String a, String b)
		{
			this.Name = a;
			this.Package = b;
		}
	}
	
	private Connection dbConnection;
	private SQLType dbType;
	
	// Default constructor is good enough for us.

	@Override
	public boolean parseConfigs(Configuration config, String worldName)
	{
		boolean isSuccess = false;
		String dbType;
		String server;
		String port;
		String database;
		String username;
		String password;
		
		Property prop;
		prop = config.get("Data.SQL", "dbType", "MySQL");
		prop.comment = "Type of the Database to connect to. Currently supports: MySQL, (more to come)";
		dbType = prop.value;
		
		prop = config.get("Data.SQL", "server", "localhost");
		prop.comment = "Server name/IP that hosts the database.";
		server = prop.value;
		
		prop = config.get("Data.SQL", "port", 3306);
		prop.comment = "Port to connect to the database on";
		port = Integer.toString(prop.getInt());
		
		prop = config.get("Data.SQL", "database", "ForgeEssentials");
		prop.comment = "Database name that FE will use to store its data & tables in. Highly reccomended to have a DB for FE data only.";
		database = prop.value;
		
		prop = config.get("Data.SQL", "username", " ");
		prop.comment = "Username to log into DB with";
		username = prop.value;
		
		prop = config.get("Data.SQL", "password", " ");
		prop.comment = "Password to log into DB with";
		password = prop.value;

		try
		{
			Class driverClass = Class.forName("com.mysql.jdbc.Driver");
			
			String connectionString = "jdbc:" + dbType + "://" + server + ":" + port + "/" + database;
			DriverManager.getConnection(connectionString, username, password);
			
			isSuccess = true;
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Unable to connect to the database!");
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			OutputHandler.SOP("Could not load the MySQL Driver! Does it exist in the lib directory?");
		}
		
		return isSuccess;
	}

	@Override
	protected void registerAdapters()
	{
		this.map.put(PlayerInfo.class, new PlayerInfoDataAdapter());

	}
	
	public Connection getConnection()
	{
		return this.dbConnection;
	}
}
