package com.ForgeEssentials.playerLogger.types;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class playerTrackerLog extends logEntry
{
	public static ArrayList<playerTrackerLog> buffer;

	public playerTrackerLogCategory cat;
	public String username;
	public String extra;

	public playerTrackerLog(playerTrackerLogCategory cat, EntityPlayer player)
	{
		super();
		this.cat = cat;
		username = player.username;
		buffer.add(this);
	}

	public playerTrackerLog()
	{
		buffer = new ArrayList();
	}

	@Override
	public String getName()
	{
		return "playerTracker";
	}

	@Override
	public String getTableCreateSQL()
	{
		return "CREATE TABLE IF NOT EXISTS " + getName()
				+ "(id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id), player CHAR(16), category CHAR(16), disciption CHAR(128), time DATETIME)";
	}

	@Override
	public String getprepareStatementSQL()
	{
		return "INSERT INTO " + getName() + " (player, category, disciption, time) VALUES (?,?,?,?);";
	}

	@Override
	public void makeEntries(Connection connection) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement(getprepareStatementSQL());
		List<playerTrackerLog> toremove = new ArrayList();
		Iterator<playerTrackerLog> i = buffer.iterator();
		while (i.hasNext())
		{
			playerTrackerLog log = i.next();
			ps.setString(1, log.username);
			ps.setString(2, log.cat.toString());
			ps.setString(3, log.extra);
			ps.setTimestamp(4, log.time);
			ps.execute();
			ps.clearParameters();
			toremove.add(log);
		}
		ps.close();
		buffer.removeAll(toremove);
	}

	public enum playerTrackerLogCategory
	{
		Login, Logout, ChangedDim, Respawn
	}
}
