package com.ForgeEssentials.playerLogger.types;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.ForgeEssentials.playerLogger.ModulePlayerLogger;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public abstract class logEntry 
{
	public Timestamp time;
	
	public logEntry()
	{
		Date date = new Date();
		this.time = new Timestamp(date.getTime());
	}
	
	public abstract String getName();
	
	public abstract String getTableCreateSQL();
	
	public abstract String getprepareStatementSQL();
	
	public abstract void makeEntries(Connection connection) throws SQLException ;
	
}
