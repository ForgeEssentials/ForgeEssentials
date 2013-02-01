package com.ForgeEssentials.auth;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.util.event.PlayerMoveEvent;

import cpw.mods.fml.common.IPlayerTracker;

public class LoginHandler implements IPlayerTracker
{
	ArrayList<String> unlogged;
	
	public LoginHandler()
	{
		unlogged = new ArrayList<String>();
	}

	public void onPlayerMove(PlayerMoveEvent event)
	{
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