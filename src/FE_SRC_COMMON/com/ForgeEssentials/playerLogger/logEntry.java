package com.ForgeEssentials.playerLogger;

import java.util.Calendar;
import java.util.Date;

import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class logEntry 
{
	public String player;
	public String time;
	public LogCatagory category;
	public String disciption;
	public WorldPoint point;
	
	public logEntry(String player, Date time, LogCatagory category, String disciption)
	{
		OutputHandler.debug("Entry made. (" + player + " > " + category.toString() + ")");
		this.player = player;
		this.time = time.toGMTString();
		this.category = category;
		this.disciption = disciption;
	}
			
	public logEntry(String player, Date time, LogCatagory category, String disciption, WorldPoint point)
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
			
	public logEntry(String player, LogCatagory category, String disciption, WorldPoint point)
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
			return "INSERT INTO logs(time, player, category, disciption) VALUES('" + time + "', '" + player + "', '" + category.toString() + "', '" + disciption + "')";
		}
		else
		{
			return "INSERT INTO logs(time, player, category, Dim, X, Y, Z, disciption) VALUES('" + time + "', '" + player + "', '" + category.toString() + "', '" + point.dim + "', '" + point.getX() + "', '" + point.getY() + "', '" + point.getZ() + "', '" + disciption + "')";
		}
	}
}
