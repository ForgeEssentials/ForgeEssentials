package com.ForgeEssentials.auth;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.IPlayerTracker;

public class LoginHandler implements IPlayerTracker
{
	public LoginHandler()
	{
		OutputHandler.info("FEauth initialized. Enabled: " + ModuleAuth.isEnabled());
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
		PlayerPassData data = PlayerPassData.getData(player.username);

		if (data != null)
		{
			if (ModuleAuth.isEnabled())
			{
				OutputHandler.chatError(player, Localization.format("command.auth.needLogin"));
			}
			ModuleAuth.unLogged.add(player.username);
		}
		else
		{
			OutputHandler.chatError(player, Localization.format("command.auth.needRegister"));
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
