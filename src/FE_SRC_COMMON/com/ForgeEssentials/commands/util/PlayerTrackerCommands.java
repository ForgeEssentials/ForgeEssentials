package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.commands.CommandMotd;
import com.ForgeEssentials.commands.CommandSetspawn;

import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTrackerCommands implements IPlayerTracker 
{
	@Override
	public void onPlayerLogin(EntityPlayer player) 
	{
		player.sendChatToPlayer(CommandMotd.motd);
		CommandSetspawn.sendToSpawn(player);
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
		CommandSetspawn.sendToSpawn(player);
	}
}
