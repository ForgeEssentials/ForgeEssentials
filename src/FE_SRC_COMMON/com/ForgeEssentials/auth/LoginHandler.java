package com.ForgeEssentials.auth;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.auth.commands.CommandLogin;
import com.ForgeEssentials.auth.commands.CommandRegister;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.IPlayerTracker;

public class LoginHandler implements IPlayerTracker
{
	public LoginHandler()
	{
		OutputHandler.info("FEauth initialized. Enabled: " + ModuleAuth.enabled);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		ModuleAuth.unLogged.remove(player.username);
		ModuleAuth.unRegistered.remove(player.username);
	}

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if (pwdSaver.isRegisted(player.username))
		{
			if (ModuleAuth.enabled)
			{
				OutputHandler.chatError(player, Localization.format("message.auth.login", new CommandLogin().getSyntaxPlayer(player)));
				ModuleAuth.unLogged.add(player.username);
			}
		}
		else
		{
			if (ModuleAuth.enabled)
			{
				OutputHandler.chatError(player, Localization.format("message.auth.register", new CommandRegister().getSyntaxPlayer(player)));
			}

			ModuleAuth.unRegistered.add(player.username);
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
