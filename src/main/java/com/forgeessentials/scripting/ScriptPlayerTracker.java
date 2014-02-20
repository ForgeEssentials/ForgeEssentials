package com.forgeessentials.scripting;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;

public class ScriptPlayerTracker implements IPlayerTracker{
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		EventType.run(player, EventType.LOGIN);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		// do nothing
		
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		EventType.run(player, EventType.RESPAWN);
	}

}