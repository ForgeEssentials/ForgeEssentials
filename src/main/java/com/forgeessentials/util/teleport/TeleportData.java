package com.forgeessentials.util.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.selections.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Uses by TeleportCenter.
 *
 * @author Dries007
 */

public class TeleportData {

	int waittime;
	private WarpPoint point;
	private EntityPlayer player;
	private WorldPoint lastPos;
	private WorldPoint currentPos;

	public TeleportData(WarpPoint point, EntityPlayer player)
	{
		this.point = point;
		this.player = player;
		waittime = TeleportCenter.getTeleportWarmup();
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
		if (waittime <= 0)
		{
			teleport();
		}
	}

	public void teleport()
	{
		try
		{
			PlayerInfo.getPlayerInfo(player.getPersistentID()).setLastTeleportOrigin(new WarpPoint(player));
			ServerConfigurationManager server = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
			if (player.dimension != point.getDimension())
			{
				server.transferPlayerToDimension((EntityPlayerMP) player, point.getDimension());
			}
			((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(point.xd, point.yd + 1, point.zd, point.yaw, point.pitch);
			if (!PermissionsManager.checkPermission(player, TeleportCenter.BYPASS_COOLDOWN))
			{
				PlayerInfo.getPlayerInfo(player.getPersistentID()).setTeleportCooldown(TeleportCenter.getTeleportCooldown());
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
