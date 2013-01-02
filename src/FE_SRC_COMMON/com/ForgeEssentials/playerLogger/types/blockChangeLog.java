package com.ForgeEssentials.playerLogger.types;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.entity.player.EntityPlayer;

public class blockChangeLog extends logEntry 
{
	public blockChangeLogCategory cat;
	public String username;
	public int dim;
	public int x;
	public int y;
	public int z;
	public String block;
	
	
	public blockChangeLog(blockChangeLogCategory cat, EntityPlayer player, String block, int X, int Y, int Z)
	{
		super();
		this.cat = cat;
		this.username = player.username;
		this.dim = player.dimension;
		this.block = block;
		this.x = X;
		this.y = Y;
		this.z = Z;
	}

	public blockChangeLog() {}

	@Override
	public String getTableName() 
	{
		return "blockChange";
	}

	@Override
	public String getTableCreateSQL() 
	{
		return "CREATE TABLE IF NOT EXISTS " + getTableName() + "(id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id), player CHAR(16), category CHAR(16), block CHAR(16), Dim INT, X INT, Y INT, Z INT, time DATETIME)";
	}

	@Override
	public String getSQL() 
	{
		return "INSERT INTO " + getTableName() + " (id, player, category, block, Dim, X, Y, Z, time) VALUES (NULL, '" + username + "', '" + cat.toString() + "', '" + block + "', '" + dim + "', '" + x + "', '" + y + "', '" + z + "', '" + time + "');";
	}
	
	public enum blockChangeLogCategory
	{
		Break, Place
	}
}
