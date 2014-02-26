package com.forgeessentials.playerlogger.types;

import java.sql.Timestamp;
import java.util.Date;

public abstract class logEntry
{
	public Timestamp	time;

	public logEntry()
	{
		Date date = new Date();
		time = new Timestamp(date.getTime());
	}

	public abstract String getName();

	public abstract String getTableCreateSQL();

	public abstract String getprepareStatementSQL();
}
