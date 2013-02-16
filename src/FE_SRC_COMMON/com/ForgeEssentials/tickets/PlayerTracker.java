package com.ForgeEssentials.tickets;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;

import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker
{
	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, ModuleTickets.PERMBASE + ".admin")))
		{
			if (!ModuleTickets.ticketList.isEmpty())
			{
				player.sendChatToPlayer(FEChatFormatCodes.DARKAQUA + "There are " + ModuleTickets.ticketList.size() + " open tickets.");
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
