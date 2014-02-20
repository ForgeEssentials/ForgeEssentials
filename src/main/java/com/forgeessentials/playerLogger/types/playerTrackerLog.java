package com.forgeessentials.playerLogger.types;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.playerLogger.ModulePlayerLogger;

public class playerTrackerLog extends logEntry
{
	public playerTrackerLog(playerTrackerLogCategory cat, EntityPlayer player, String extra)
	{
		super();
		String ip = ((EntityPlayerMP) player).playerNetServerHandler.netManager.getSocketAddress().toString().substring(1);
		ip = ip.substring(0, ip.lastIndexOf(":"));
		
		try
		{
			PreparedStatement ps = ModulePlayerLogger.getConnection().prepareStatement(getprepareStatementSQL());
			ps.setString(1, player.username);
			ps.setString(2, cat.toString());
			ps.setString(3, extra);
			ps.setTimestamp(4, time);
			ps.setString(5, ip);
			ps.execute();
			ps.clearParameters();
			ps.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public playerTrackerLog()
	{
		super();
	}

	@Override
	public String getName()
	{
		return "playerTracker";
	}

	@Override
	public String getTableCreateSQL()
	{
		return "CREATE TABLE IF NOT EXISTS " + getName() + "(id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id), player CHAR(16), category CHAR(16), disciption CHAR(128), time DATETIME, ip CHAR(40))";
	}

	@Override
	public String getprepareStatementSQL()
	{
		return "INSERT INTO " + getName() + " (player, category, disciption, time, ip) VALUES (?,?,?,?,?);";
	}

	public enum playerTrackerLogCategory
	{
		Login, Logout, ChangedDim, Respawn
	}
}
