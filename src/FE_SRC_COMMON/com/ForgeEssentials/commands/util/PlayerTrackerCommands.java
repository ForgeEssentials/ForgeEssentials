package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.commands.CommandMotd;

import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTrackerCommands implements IPlayerTracker {

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		player.sendChatToPlayer(CommandMotd.motd);
		}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

}
