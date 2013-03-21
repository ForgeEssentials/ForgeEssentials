package com.ForgeEssentials.auth.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.auth.ModuleAuth;
import com.ForgeEssentials.auth.PlayerPassData;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRegister extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "register";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length != 1)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
		if (ModuleAuth.unLogged.contains(sender.username))
		{
			OutputHandler.chatError(sender, Localization.get("command.register.already"));
			return;
		}
		else if (ModuleAuth.unRegistered.contains(sender.username))
		{
			if (!ModuleAuth.vanillaMode() || ModuleAuth.allowOfflineReg)
			{
				register(sender, args[0]);
			}
		}
	}

	public void register(EntityPlayer sender, String pass)
	{
		try
		{
			pass = ModuleAuth.encrypt(pass);
			PlayerPassData data = new PlayerPassData(sender.username, pass);
			PlayerPassData.registerData(data);
			PlayerPassData.discardData(sender.username);
			OutputHandler.chatError(sender, Localization.get("command.register.register"));
			ModuleAuth.unRegistered.remove(sender.username);
		}
		catch (Exception e)
		{
			OutputHandler.chatError(sender, e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return null;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}
}
