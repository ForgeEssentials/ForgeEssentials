package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

/**
 * Use this for all TPs. This system does it all for you: warmup, cooldown,
 * bypass for both, going between dimensions.
 * @author Dries007
 */

public class TeleportCenter implements IScheduledTickHandler
{
	private static ArrayList<TPdata>	queue				= new ArrayList<TPdata>();
	private static ArrayList<TPdata>	removeQueue		= new ArrayList<TPdata>();

	public static int					tpWarmup;
	public static int					tpCooldown;

	public static final String			BYPASS_WARMUP	= "fe.teleport.bypasswarmup";
	public static final String			BYPASS_COOLDOWN	= "fe.teleport.bypasscooldown";

	public static void addToTpQue(WarpPoint point, EntityPlayer player)
	{
		if (PlayerInfo.getPlayerInfo(player.username).TPcooldown != 0 && !APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(player, BYPASS_COOLDOWN)))
		{
			ChatUtils.sendMessage(player,String.format("Cooldown still active. %s seconds to go.", FunctionHelper.parseTime(PlayerInfo.getPlayerInfo(player.username).TPcooldown)));
		}
		else
		{
			PlayerInfo.getPlayerInfo(player.username).TPcooldown = tpCooldown;
			TPdata data = new TPdata(point, player);
			if (tpWarmup == 0 || APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(player, BYPASS_WARMUP)))
			{
				data.doTP();
			}
			else
			{
				ChatUtils.sendMessage(player, String.format("Teleporting, please stand still for %s seconds.", FunctionHelper.parseTime(tpWarmup)));
				queue.add(data);
			}
		}
	}

	public static void abort(TPdata tpData)
	{
		removeQueue.add(tpData);
		ChatUtils.sendMessage(tpData.getPlayer(), "Teleport cancelled.");
	}

	public static void TPdone(TPdata tpData)
	{
		removeQueue.add(tpData);
		ChatUtils.sendMessage(tpData.getPlayer(), "Teleported.");
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		for (TPdata data : queue)
		{
			data.count();
		}
		queue.removeAll(removeQueue);
		removeQueue.clear();
		for (Object player : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
		{
			PlayerInfo.getPlayerInfo(((EntityPlayer) player).username).TPcooldownTick();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		// Not needed here
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return "TeleportCenter";
	}

	@Override
	public int nextTickSpacing()
	{
		return 20;
	}

}
