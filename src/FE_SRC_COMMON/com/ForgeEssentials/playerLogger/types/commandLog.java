package com.ForgeEssentials.playerLogger.types;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class commandLog extends logEntry
{
	public String username;
	public String command;

	public commandLog(String sender, String command)
	{
		super();
		username = sender;
		this.command = command;
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
		return "CREATE TABLE IF NOT EXISTS " + getName()
				+ "(id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id), sender CHAR(64), command CHAR(128), time DATETIME)";
	}

	@Override
	public String getprepareStatementSQL()
	{
		return "INSERT INTO " + getName() + " (sender, command, time) VALUES (?,?,?);";
	}

	@Override
	public void makeEntries(Connection connection, List<logEntry> buffer) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement(getprepareStatementSQL());
		Iterator<logEntry> i = buffer.iterator();
		while (i.hasNext())
		{
			logEntry obj = i.next();
			if(obj instanceof commandLog)
			{
				commandLog log = (commandLog) obj;
				ps.setString(1, log.username);
				ps.setString(2, log.command);
				ps.setTimestamp(3, log.time);
				ps.execute();
				ps.clearParameters();
			}
		}
		ps.close();
	}
}
