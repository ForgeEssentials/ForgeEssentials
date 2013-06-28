package com.ForgeEssentials.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.util.OutputHandler;

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