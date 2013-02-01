package com.ForgeEssentials.auth;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.event.PlayerMoveEvent;

import cpw.mods.fml.common.IPlayerTracker;

public class LoginHandler implements IPlayerTracker
{
	ArrayList<String> unlogged;
	
	public LoginHandler()
	{
		unlogged = new ArrayList<String>();
	}

	@ForgeSubscribe(priority=EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (!ModuleAuth.enabled)
			return;
		
		if (unlogged.contains(event.entityPlayer.username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, "Please use /login to login");
		}
	}
	
	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		unlogged.remove(player.username);
	}

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		unlogged.add(player.username);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player){}

	@Override
	public void onPlayerRespawn(EntityPlayer player){}
}