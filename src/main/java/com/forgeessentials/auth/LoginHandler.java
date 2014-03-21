package com.forgeessentials.auth;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.IPlayerTracker;

public class LoginHandler implements IPlayerTracker
{
	public LoginHandler()
	{
		OutputHandler.felog.info("FEauth initialized. Enabled: " + ModuleAuth.isEnabled());
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		ModuleAuth.unLogged.remove(player.username);
		ModuleAuth.unRegistered.remove(player.username);
		PlayerPassData.discardData(player.username);
	}

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if (!ModuleAuth.isEnabled()){
			return;
		}
		PlayerPassData data = PlayerPassData.getData(player.username);

		if (data == null)
		{
			OutputHandler.chatError(player, "Registration required. Try /auth help.");
			ModuleAuth.unRegistered.add(player.username);
		}
		else
		{
			OutputHandler.chatError(player, "Login required. Try /auth help.");
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
