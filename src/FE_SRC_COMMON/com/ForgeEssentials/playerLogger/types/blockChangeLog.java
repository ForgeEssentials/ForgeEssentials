package com.ForgeEssentials.playerLogger.types;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.ForgeEssentials.playerLogger.ModulePlayerLogger;

import net.minecraft.entity.player.EntityPlayer;

public class blockChangeLog extends logEntry 
{
	public static ArrayList<blockChangeLog> buffer;
	
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
		buffer.add(this);
	}
	
	public blockChangeLog() {buffer = new ArrayList();}
	
	@Override
	public String getName() 
	{
		return "blockChange";
	}

	@Override
	public String getTableCreateSQL() 
	{
		return "CREATE TABLE IF NOT EXISTS " + getName() + "(id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id), player CHAR(16), category CHAR(16), block CHAR(16), Dim INT, X INT, Y INT, Z INT, time DATETIME)";
	}
	
	@Override
	public String getprepareStatementSQL() 
	{
		return "INSERT INTO " + getName() + " (player, category, block, Dim, X, Y, Z, time) VALUES (?,?,?,?,?,?,?,?);";
	}

	@Override
	public void makeEntries(Connection connection) throws SQLException 
	{
		PreparedStatement ps = connection.prepareStatement(getprepareStatementSQL());
		Iterator<blockChangeLog> i = buffer.iterator();
		List<blockChangeLog> toremove = new ArrayList();
		while(i.hasNext())
		{
			blockChangeLog log = i.next();
			ps.setString(1, log.username);
			ps.setString(2, log.cat.toString());
			ps.setString(3, log.block);
			ps.setInt(4, log.dim);
			ps.setInt(5, log.x);
			ps.setInt(6, log.y);
			ps.setInt(7, log.z);
			ps.setTimestamp(8, log.time);
			ps.execute();
			ps.clearParameters();
			toremove.add(log);
		}
		ps.close();
		buffer.removeAll(toremove);
	}
	
	public enum blockChangeLogCategory
	{
		broke, placed
	}
}
