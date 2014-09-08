package com.forgeessentials.permissions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.server.CommandHandlerForge;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.PlayerChangedZone;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PermsEventHandler {
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onZoneChange(PlayerChangedZone event)
	{
		String query1 = APIRegistry.permissionManager.getPermissionProperty(event.entityPlayer, event.beforeZone, "fe.perm.zone.exit");
		if (query1 != null)
		{
			ChatUtils.sendMessage(event.entityPlayer, FunctionHelper.formatColors(query1));
		}
		String query2 = APIRegistry.permissionManager.getPermissionProperty(event.entityPlayer, event.afterZone, "fe.perm.zone.entry");
		if (query2 != null)
		{
			ChatUtils.sendMessage(event.entityPlayer, FunctionHelper.formatColors(query2));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void checkCommandPerm(CommandEvent e)
	{
		if (!(e.sender instanceof EntityPlayer))
		{
			return;
		}
		else if (!CommandHandlerForge.canUse(e.command, e.sender))
		{
			e.setCanceled(true);
		}
	}
}