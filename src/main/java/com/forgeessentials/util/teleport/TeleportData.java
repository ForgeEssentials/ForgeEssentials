package com.forgeessentials.util.teleport;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Uses by TeleportCenter.
 *
 * @author Dries007
 */

public class TeleportData {

	private WarpPoint point;
	private EntityPlayerMP player;
	private WarpPoint lastPos;
	private long startTime;

	public TeleportData(WarpPoint point, EntityPlayerMP player)
	{
		this.point = point;
		this.player = player;
		startTime = System.currentTimeMillis();
		lastPos = new WarpPoint(player);
	}

	public boolean checkTeleport()
	{
		if (!lastPos.equals(new WarpPoint(player)))
		{
			OutputHandler.chatWarning(player, "Teleport cancelled.");
			return true;
		}

		if ((System.currentTimeMillis() - startTime) / 1000L > TeleportCenter.getTeleportWarmup())
		{
			teleport();
			return true;
		}
		else
		{
			return false;
		}
	}

	public void teleport()
	{
		PlayerInfo pi = PlayerInfo.getPlayerInfo(player.getPersistentID());
		pi.setLastTeleportOrigin(new WarpPoint(player));
		pi.setLastTeleportTime(System.currentTimeMillis());

		if (player.dimension != point.getDimension())
		{
			FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager()
					.transferPlayerToDimension(player, point.getDimension());
		}
		player.playerNetServerHandler.setPlayerLocation(point.getX(), point.getY() + 0.1, point.getZ(), point.getYaw(), point.getPitch());
		OutputHandler.chatConfirmation(player, "Teleported.");
	}

	public EntityPlayerMP getPlayer()
	{
		return player;
	}

}
