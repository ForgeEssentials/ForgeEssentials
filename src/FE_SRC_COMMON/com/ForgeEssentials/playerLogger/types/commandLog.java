package com.ForgeEssentials.playerLogger.types;

import net.minecraft.entity.player.EntityPlayer;

public class commandLog extends logEntry 
{
	public String username;
	public String command;
	
	public commandLog(String sender, String command)
	{
		super();
		this.username = sender;
		this.command = command;
	}
	
	public commandLog() {}

	@Override
	public String getTableName() 
	{
		return "commands";
	}

	@Override
	public String getTableCreateSQL() 
	{
		return "CREATE TABLE IF NOT EXISTS " + getTableName() + "(id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id), sender CHAR(64), command CHAR(128), time DATETIME)";
	}

	@Override
	public String getSQL() 
	{
		return "INSERT INTO " + getTableName() + " (id, sender, command, time) VALUES (NULL, '" + username + "', '" + command + "', '" + time + "');";
	}
}
