package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;

public class EventHandler
{
	@ForgeSubscribe()
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		/*
		 * Lb info!
		 */
		
		if(e.entityPlayer.getEntityData().getBoolean("lb"))
		{
			e.setCanceled(true);
			e.entityPlayer.sendChatToPlayer("Results:");
			try 
			{
				Date date = new Date();
				Timestamp time = new Timestamp(date.getTime());
				Connection connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
				Statement st = connection.createStatement();
				st.execute("SELECT * FROM " +
						" `blockchange` WHERE  " +
						"`Dim` = " + e.entityPlayer.dimension + 
						" AND  `X` = " + e.x + " AND  `Y` = " + e.y + 
						" AND  `Z` = " + e.z + " AND  `time` >  '2013-01-01 00:00:00' LIMIT 0 , 30");
				ResultSet res = st.getResultSet();
				while (res.next()) 
				{
			        e.entityPlayer.sendChatToPlayer(res.getString("player"));
				}
				
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
