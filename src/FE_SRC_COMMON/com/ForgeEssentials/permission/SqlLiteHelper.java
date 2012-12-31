package com.ForgeEssentials.permission;

import com.ForgeEssentials.util.OutputHandler;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlLiteHelper
{
	private static File file = new File(ModulePermissions.permsFolder, "permissions.db");
	
	private String DriverClass = "org.sqlite.JDBC";
	private Connection dbConnection;
	private boolean generate;
	
	public SqlLiteHelper()
	{
		
	}
	
	protected void connect() throws SQLException, ClassNotFoundException
	{
		try
		{
			Class driverClass = Class.forName(DriverClass);

			this.dbConnection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
		}
		catch (SQLException e)
		{
			OutputHandler.SOP("Unable to connect to the database!");
			throw e;
		}
		catch (ClassNotFoundException e)
		{
			OutputHandler.SOP("Could not load the SQLite JDBC Driver! Does it exist in the lib directory?");
			throw e;
		}
	}
}
