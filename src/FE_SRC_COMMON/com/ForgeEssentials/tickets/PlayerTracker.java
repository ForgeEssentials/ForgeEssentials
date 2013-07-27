package com.ForgeEssentials.tickets;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;

import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker
{
	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(player, ModuleTickets.PERMBASE + ".admin")))
		{
			if (!ModuleTickets.ticketList.isEmpty()) {
                ChatUtils.sendMessage(player, FEChatFormatCodes.DARKAQUA + "There are " + ModuleTickets.ticketList.size() + " open tickets.");
            }
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{

	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{

	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{

	}
}
