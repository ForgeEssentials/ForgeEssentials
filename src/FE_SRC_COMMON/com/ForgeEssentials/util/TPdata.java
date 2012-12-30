package com.ForgeEssentials.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Uses by TeleportCenter.
 * 
 * @author Dries007
 *
 */

public class TPdata 
{
	private WarpPoint point;
	private EntityPlayer player;
	private WorldPoint lastPos;
	private WorldPoint currentPos;
	int waittime;
	
	public TPdata(WarpPoint point, EntityPlayer player)
	{
		this.point = point;
		this.player = player;
		this.waittime = TeleportCenter.tpWarmup;
		lastPos = new WorldPoint(player.dimension, (int) player.posX, (int) player.posY, (int) player.posZ);
	}
	
	public void count()
	{
		currentPos = new WorldPoint(player.dimension, (int) player.posX, (int) player.posY, (int) player.posZ);
		if(!lastPos.equals(currentPos))
		{
			TeleportCenter.abort(this);
		}
		
		waittime --;
		if(waittime == 0)
		{
			doTP();
		}
	}
	
	public void doTP()
	{
		PlayerInfo.getPlayerInfo((EntityPlayer) player).back = new WorldPoint(player);
		ServerConfigurationManager server = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
		if(player.dimension != point.dim)
		{
			server.transferPlayerToDimension((EntityPlayerMP) player, point.dim);
		}
		((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(point.x, point.y, point.z, point.yaw, point.pitch);
		PlayerInfo.getPlayerInfo((EntityPlayer) player).TPcooldown = TeleportCenter.tpCooldown;
		TeleportCenter.TPdone(this);
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}
}
