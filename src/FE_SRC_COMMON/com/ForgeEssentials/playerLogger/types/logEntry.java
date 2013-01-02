package com.ForgeEssentials.playerLogger.types;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public abstract class logEntry 
{
	public String time;
	
	public logEntry()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		this.time = dateFormat.format(date);
	}
	
	public abstract String getTableName();
	
	public abstract String getTableCreateSQL();
	
	public abstract String getSQL();
}
