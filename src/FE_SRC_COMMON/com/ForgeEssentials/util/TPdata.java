package com.ForgeEssentials.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.AreaSelector.WarpPoint;
import com.ForgeEssentials.api.AreaSelector.WorldPoint;
import com.ForgeEssentials.core.PlayerInfo;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Uses by TeleportCenter.
 * @author Dries007
 */

public class TPdata
{
	private WarpPoint		point;
	private EntityPlayer	player;
	private WorldPoint		lastPos;
	private WorldPoint		currentPos;
	int						waittime;

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
		try
		{
			PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
			ServerConfigurationManager server = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
			if (player.dimension != point.dim)
			{
				server.transferPlayerToDimension((EntityPlayerMP) player, point.dim);
			}
			((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(point.xd, point.yd + 1, point.zd, point.yaw, point.pitch);
			if (!APIRegistry.perms.checkPermAllowed(player, TeleportCenter.BYPASS_COOLDOWN))
			{
				PlayerInfo.getPlayerInfo(player.username).TPcooldown = TeleportCenter.tpCooldown;
			}
			TeleportCenter.TPdone(this);
		}
		catch (Exception e)
		{
			OutputHandler.felog.warning("Someone tried to crash the server when warping!");
			e.printStackTrace();
		}
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}
}
