package com.forgeessentials.util.teleport;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Use this for all TPs. This system does it all for you: warmup, cooldown, bypass for both, going between dimensions.
 *
 * @author Dries007
 */

public class TeleportCenter {

	public static final String BYPASS_WARMUP = "fe.teleport.bypasswarmup";
	public static final String BYPASS_COOLDOWN = "fe.teleport.bypasscooldown";

	private static int teleportWarmup = 3;
	private static int teleportCooldown = 5;

	private static ArrayList<TeleportData> queue = new ArrayList<TeleportData>();

	@SuppressWarnings("unused")
    private static final TeleportCenter instance = new TeleportCenter();

	private TeleportCenter()
	{
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	public static void teleport(WarpPoint point, EntityPlayerMP player)
	{
		PlayerInfo pi = PlayerInfo.getPlayerInfo(player.getPersistentID());
		long timeSinceLastTeleport = (System.currentTimeMillis() - pi.getLastTeleportTime()) / 1000L;
		if (timeSinceLastTeleport < teleportCooldown && timeSinceLastTeleport >= 0 && !PermissionsManager.checkPermission(player, BYPASS_COOLDOWN))
		{
			OutputHandler.chatNotification(player, String.format("Cooldown still active. %s seconds to go.", teleportCooldown - timeSinceLastTeleport));
		}
		else
		{
			TeleportData data = new TeleportData(point, player);
			if (teleportWarmup == 0 || PermissionsManager.checkPermission(player, BYPASS_WARMUP))
			{
				data.teleport();
			}
			else
			{
				OutputHandler.chatNotification(player, String.format("Teleporting, please stand still for %s seconds.", FunctionHelper.parseTime(teleportWarmup)));
				queue.add(data);
			}
		}
	}

	@SubscribeEvent
	public void tickStart(TickEvent.ServerTickEvent e)
	{
		for (Iterator<TeleportData> tpData = queue.iterator(); tpData.hasNext();)
		{
			TeleportData teleportData = tpData.next();
			if (teleportData.checkTeleport())
				tpData.remove();
		}
	}

	public static int getTeleportWarmup()
	{
		return teleportWarmup;
	}

	public static void setTeleportWarmup(int teleportWarmup)
	{
		TeleportCenter.teleportWarmup = teleportWarmup;
	}

	public static int getTeleportCooldown()
	{
		return teleportCooldown;
	}

	public static void setTeleportCooldown(int teleportCooldown)
	{
		TeleportCenter.teleportCooldown = teleportCooldown;
	}

}
