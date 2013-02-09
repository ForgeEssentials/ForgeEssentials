package com.ForgeEssentials.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

/**
 * Use this for all TPs. This system does it all for you: warmup, cooldown,
 * bypass for both, going between dimensions.
 * 
 * @author Dries007
 * 
 */

public class TeleportCenter implements IScheduledTickHandler
{
	public static HashMap<String, Warp>						warps			= new HashMap<String, Warp>();
	public static HashMap<String, HashMap<String, PWarp>>	pwMap			= new HashMap<String, HashMap<String, PWarp>>();

	private static ArrayList<TPdata>						que				= new ArrayList();
	private static ArrayList<TPdata>						removeQue		= new ArrayList();

	public static int										tpWarmup;
	public static int										tpCooldown;

	public static final String								BYPASS_WARMUP	= "ForgeEssentials.TeleportCenter.BypassWarmup";
	public static final String								BYPASS_COOLDOWN	= "ForgeEssentials.TeleportCenter.BypassCooldown";

	public static void addToTpQue(WarpPoint point, EntityPlayer player)
	{
		if (PlayerInfo.getPlayerInfo(player).TPcooldown != 0 && !PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, BYPASS_COOLDOWN)))
		{
			player.sendChatToPlayer(Localization.get(Localization.TC_COOLDOWN).replaceAll("%c", "" + PlayerInfo.getPlayerInfo(player).TPcooldown));
		}
		else
		{
			PlayerInfo.getPlayerInfo(player).TPcooldown = tpCooldown;
			TPdata data = new TPdata(point, player);
			if (tpWarmup == 0 || PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, BYPASS_WARMUP)))
			{
				data.doTP();
			}
			else
			{
				player.sendChatToPlayer(Localization.get(Localization.TC_WARMUP).replaceAll("%w", "" + tpWarmup));
				que.add(data);
			}
		}
	}

	public static void abort(TPdata tpData)
	{
		removeQue.add(tpData);
		tpData.getPlayer().sendChatToPlayer(Localization.get(Localization.TC_ABORTED));
	}

	public static void TPdone(TPdata tpData)
	{
		removeQue.add(tpData);
		tpData.getPlayer().sendChatToPlayer(Localization.get(Localization.TC_DONE));
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		for (TPdata data : que)
		{
			data.count();
		}
		que.removeAll(removeQue);
		removeQue.clear();
		for (Object player : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
		{
			PlayerInfo.getPlayerInfo((EntityPlayer) player).TPcooldownTick();
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
