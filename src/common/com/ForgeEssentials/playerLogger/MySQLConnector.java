package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.ForgeEssentials.playerLogger.ModulePlayerLogger.logEntry;
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
				OutputHandler.SOP("########################################");
				OutputHandler.SOP("### Where is your MySQL JDBC Driver? ");
				OutputHandler.SOP("### No driver means no PlayerLogger!");
				OutputHandler.SOP("########################################");
				return;
			}
			DBcon = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
		}
		catch (Exception ex1) 
		{
			OutputHandler.SOP("Cannot connect to database server");
			if(ModulePlayerLogger.ragequit) throw new RuntimeException("Cannot connect to database server");
		}
	}
	
	public void makeTable()
	{
		try 
		{
			s = DBcon.createStatement();
			s.executeUpdate ("CREATE TABLE logs (id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id),time CHAR(64), player CHAR(64), category CHAR(64), disciption CHAR(128))");
			OutputHandler.debug("Table made.");
		}
		catch (SQLException ex2) 
		{
			OutputHandler.debug("Table already exists");
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
			OutputHandler.SOP("Fail closing connection");
			OutputHandler.SOP(ex.getMessage());
		}
	}

	public void makeLog(logEntry log) 
	{
		try 
		{
			s = DBcon.createStatement();
			s.executeUpdate("INSERT INTO logs(time, player, category, disciption) VALUES('" + log.time + "', '" + log.player + "', '" + log.category + "', '" + log.disciption + "')");
			OutputHandler.debug("Entry made.");
		}
		catch (SQLException ex2) 
		{
			OutputHandler.debug("Error logging data!");
			if(ModulePlayerLogger.ragequit) throw new RuntimeException("Cannot connect to database server");
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
			}
		}
	}
}
