package com.ForgeEssentials.playerLogger.logger.types;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ForgeEssentials.playerLogger.ModulePlayerLogger;

public class commandLog extends logEntry
{
	public commandLog(String sender, String command)
	{
		super();
		
		try
		{
			PreparedStatement ps = ModulePlayerLogger.getConnection().prepareStatement(getprepareStatementSQL());
			ps.setString(1, sender);
			ps.setString(2, command);
			ps.setTimestamp(3, time);
			ps.execute();
			ps.clearParameters();
			ps.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			ModulePlayerLogger.error(e);
		}
		
	}

	public commandLog()
	{
		super();
	}

	@Override
	public String getName()
	{
		return "commands";
	}

	@Override
	public String getTableCreateSQL()
	{
		return "CREATE TABLE IF NOT EXISTS " + getName() + "(id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id), sender CHAR(64), command CHAR(128), time DATETIME)";
	}

	@Override
	public String getprepareStatementSQL()
	{
		return "INSERT INTO " + getName() + " (sender, command, time) VALUES (?,?,?);";
	}
}
