package com.ForgeEssentials.playerLogger;

import java.util.Calendar;
import java.util.Date;

import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

public class logEntry 
{
	public String player;
	public String time;
	public LogCatagory category;
	public String disciption;
	public Point point;
	
	public logEntry(String player, Date time, LogCatagory category, String disciption)
	{
		OutputHandler.debug("Entry made. (" + player + " > " + category.toString() + ")");
		this.player = player;
		this.time = time.toGMTString();
		this.category = category;
		this.disciption = disciption;
	}
			
	public logEntry(String player, Date time, LogCatagory category, String disciption, Point point)
	{
		OutputHandler.debug("Entry made. (" + player + " > " + category.toString() + ")");
		this.player = player;
		this.time = time.toGMTString();
		this.category = category;
		this.disciption = disciption;
		this.point = point;
	}
	
	public logEntry(String player, LogCatagory category, String disciption)
	{
		OutputHandler.debug("Entry made. (" + player + " > " + category.toString() + ")");
		this.player = player;
		this.time = Calendar.getInstance().getTime().toGMTString();
		this.category = category;
		this.disciption = disciption;
	}
			
	public logEntry(String player, LogCatagory category, String disciption, Point point)
	{
		OutputHandler.debug("Entry made. (" + player + " > " + category.toString() + ")");
		this.player = player;
		this.time = Calendar.getInstance().getTime().toGMTString();
		this.category = category;
		this.disciption = disciption;
		this.point = point;
	}

	public String getSQL() 
	{
		if(point == null)
		{
			return "INSERT INTO logs(time, player, category, X, Y, Z, disciption) VALUES('" + time + "', '" + player + "', '" + category.toString() + "', NULL, NULL, NULL, '" + disciption + "')";
		}
		else
		{
			return "INSERT INTO logs(time, player, category, X, Y, Z, disciption) VALUES('" + time + "', '" + player + "', '" + category.toString() + "', '" + point.getX() + "', '" + point.getY() + "', '" + point.getZ() + "', '" + disciption + "')";
		}
	}
}
