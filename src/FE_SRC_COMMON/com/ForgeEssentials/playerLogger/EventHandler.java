package com.ForgeEssentials.playerLogger;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class EventHandler
{
	@ForgeSubscribe()
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		/*
		 * Lb info!
		 */

		if (e.entityPlayer.getEntityData().getBoolean("lb"))
		{
			e.setCanceled(true);
			try
			{
				int limit = e.entityPlayer.getEntityData().getInteger("lb_limit");
				Date date = new Date();
				Timestamp time = new Timestamp(date.getTime());
				Connection connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
				Statement st = connection.createStatement();
				st.execute("SELECT * FROM  `blockChange` WHERE  `Dim` = " + e.entityPlayer.dimension + " AND  `X` = " + e.x + " AND  `Y` = " + e.y
						+ " AND  `Z` = " + e.z + " ORDER BY id DESC LIMIT " + limit);
				ResultSet res = st.getResultSet();

				e.entityPlayer.sendChatToPlayer("Results:");

				while (res.next())
				{
					e.entityPlayer.sendChatToPlayer(res.getString("player") + " " + res.getString("category") + " block " + res.getString("block") + " at "
							+ res.getTimestamp("time"));
				}
				res.close();
				st.close();
				connection.close();
			}
			catch (SQLException e1)
			{
				e.entityPlayer.sendChatToPlayer("Connection error!");
				e1.printStackTrace();
			}

			e.entityPlayer.getEntityData().setBoolean("lb", false);
		}
	}
}
