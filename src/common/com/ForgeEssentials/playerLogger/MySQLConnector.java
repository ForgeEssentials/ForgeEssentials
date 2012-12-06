package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.ForgeEssentials.core.CoreConfig;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.util.OutputHandler;

public class MySQLConnector 
{
	Connection DBcon = null;
	Statement s = null;
	
	public MySQLConnector()
	{
		try 
		{
			try 
			{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			}
			catch (ClassNotFoundException error)
			{
				OutputHandler.felog.severe("Could not find MySQL JDBC Driver. PlayerLogger module disabled.");
				OutputHandler.SOP("An error was caught loading the MySQL database. Look in ForgeModLoader-server-0.log for more details.");
				ModulePlayerLogger.ragequit();
				ModuleLauncher.loggerEnabled = false;
				return;
			}
			DBcon = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
			OutputHandler.debug("Connected to DB");
			
		}
		catch (Exception ex1) 
		{
			OutputHandler.SOP("An error was caught loading the MySQL database. Look in ForgeModLoader-server-0.log for more details.");
			OutputHandler.felog.severe("Could not connect to the database server.");
			OutputHandler.felog.severe("Ensure the server is online and your login info has been properly configured.");
			OutputHandler.felog.severe("This can be configured in playerlogger.cfg in your ForgeEssentials folder.");
			ModulePlayerLogger.ragequit();
			ModuleLauncher.loggerEnabled = false;
		}
	}
	
	public void makeTable()
	{
		try 
		{
			s = DBcon.createStatement();
			
			// TODO For debug ONLY. Clears table so you get clear results
			boolean clearTable = true;
			if(ModulePlayerLogger.DEBUG && clearTable)
			{
				s.executeUpdate("DROP TABLE IF EXISTS logs");
			}
			s.executeUpdate ("CREATE TABLE logs (id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id),time CHAR(64), player CHAR(64), category CHAR(64),Dim INT, X INT, Y INT, Z INT, disciption CHAR(128))");
			OutputHandler.debug("Connected to DB");
			
		}
		catch (SQLException ex2)
		{
			OutputHandler.debug("Connected to DB");
			
		}
		finally
		{
			try
			{
				s.close();
			}
			catch (Exception e) 
			{
				OutputHandler.SOP("Error closing the statement");
				OutputHandler.debug(e.getMessage());
				
				}
		}
	}
	
	public void close()
	{
		try 
		{
			if (DBcon != null) DBcon.close();
		}
		catch (SQLException ex)
		{
			OutputHandler.SOP("Could not close connection");
			OutputHandler.debug(ex.getMessage());
			
			
		}
	}

	public boolean makeLog(logEntry log) 
	{
		try 
		{
			s = DBcon.createStatement();
			s.executeUpdate(log.getSQL());
			OutputHandler.debug("Entry made. (" + log.player + " > " + log.category.toString() + ")");
			
			
		}
		catch (SQLException ex2) 
		{
			OutputHandler.SOP("Error logging data!");
			OutputHandler.debug(ex2.getMessage());
			
			ModulePlayerLogger.ragequit();
			return false;
		}
		finally
		{
			try 
			{
				s.close();
			}
			catch (SQLException e) 
			{
				OutputHandler.SOP("Error closing the statement");
				OutputHandler.debug(e.getMessage());
				
				return false;
			}
		}
		return true;
	}
}
