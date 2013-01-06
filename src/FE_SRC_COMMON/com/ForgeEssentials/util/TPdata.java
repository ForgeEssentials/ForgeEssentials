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
		waittime = TeleportCenter.tpWarmup;
		lastPos = new WarpPoint(player);
	}

	public void count()
	{
		currentPos = new WarpPoint(player);
		if (!lastPos.equals(currentPos))
		{
			TeleportCenter.abort(this);
		}

		waittime--;
		if (waittime == 0)
		{
			doTP();
		}
	}

	public void doTP()
	{
		PlayerInfo.getPlayerInfo(player).back = new WarpPoint(player);
		ServerConfigurationManager server = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
		if (player.dimension != point.dim)
		{
			server.transferPlayerToDimension((EntityPlayerMP) player, point.dim);
		}
		((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(point.x, point.y, point.z, point.yaw, point.pitch);
		PlayerInfo.getPlayerInfo(player).TPcooldown = TeleportCenter.tpCooldown;
		TeleportCenter.TPdone(this);
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}
}
