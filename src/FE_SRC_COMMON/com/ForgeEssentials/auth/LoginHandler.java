package com.ForgeEssentials.auth;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.event.PlayerMoveEvent;

import cpw.mods.fml.common.IPlayerTracker;

public class LoginHandler implements IPlayerTracker
{
	ArrayList<String>	unlogged;
	ArrayList<String>	notRegisted;

	public LoginHandler()
	{
		unlogged = new ArrayList<String>();
		notRegisted = new ArrayList<String>();
		OutputHandler.info("FEauth initialized. Enabled: " + ModuleAuth.enabled);
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (!ModuleAuth.enabled)
		{
			return;
		}

		if (unlogged.contains(event.entityPlayer.username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, "Please use '/login <pwd>' to login");
		}

		if (notRegisted.contains(event.entityPlayer.username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, "Please use '/register <pwd>' to register");
		}
	}

	public void login(EntityPlayer player)
	{
		player.sendChatToPlayer("Successfully logged in.");
		unlogged.remove(player.username);
		notRegisted.remove(player.username);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		unlogged.remove(player.username);
		notRegisted.remove(player.username);
	}

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if (pwdSaver.isRegisted(player.username))
		{
			if (ModuleAuth.enabled)
			{
				player.sendChatToPlayer("Please use '/login <pwd>' to login");
				unlogged.add(player.username);
			}
		}
		else
		{
			player.sendChatToPlayer("You must '/register <pwd>'!");
			notRegisted.add(player.username);
		}
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