package com.ForgeEssentials.playerLogger.types;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public abstract class logEntry
{
	public Timestamp time;

	public logEntry()
	{
		Date date = new Date();
		time = new Timestamp(date.getTime());
	}

	public abstract String getName();

	public abstract String getTableCreateSQL();

	public abstract String getprepareStatementSQL();

	public abstract void makeEntries(Connection connection) throws SQLException;

}
