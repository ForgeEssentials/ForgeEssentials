package com.ForgeEssentials.playerLogger.types;

import net.minecraft.entity.player.EntityPlayer;

public class playerTrackerLog extends logEntry 
{
	public playerTrackerLogCategory cat;
	public String username;
	public String extra;
	
	public playerTrackerLog(playerTrackerLogCategory cat, EntityPlayer player)
	{
		super();
		this.cat = cat;
		this.username = player.username;
	}
	
	public playerTrackerLog() {}

	@Override
	public String getTableName() 
	{
		return "playerTracker";
	}

	@Override
	public String getTableCreateSQL() 
	{
		return "CREATE TABLE IF NOT EXISTS " + getTableName() + "(id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id), player CHAR(64), category CHAR(16), disciption CHAR(128), time DATETIME)";
	}

	@Override
	public String getSQL() 
	{
		if(extra == null)return "INSERT INTO " + getTableName() + " (id, player, category, disciption, time) VALUES (NULL, '" + username + "', '" + cat.toString() + ", NULL, '" + time + "');";
		else return "INSERT INTO " + getTableName() + " (id, player, category, disciption, time) VALUES (NULL, '" + username + "', '" + cat.toString() + "', '" + extra + "', '" + time + "');";
	}
	
	public enum playerTrackerLogCategory
	{
		Login, Logout, ChangedDim, Respawn
	}
}
