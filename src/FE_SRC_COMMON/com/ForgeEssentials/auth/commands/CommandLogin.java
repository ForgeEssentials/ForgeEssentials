package com.ForgeEssentials.auth.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.auth.ModuleAuth;
import com.ForgeEssentials.auth.PlayerPassData;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandLogin extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "login";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length != 1)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
		else if (ModuleAuth.unRegistered.contains(sender.username))
		{
			OutputHandler.chatError(sender, Localization.get("command.login.register"));
		}
		else if (ModuleAuth.unLogged.contains(sender.username))
		{
			PlayerPassData data = PlayerPassData.getData(sender.username);

			if (data == null)
			{
				OutputHandler.chatError(sender, Localization.get("command.login.already"));
				return;
			}

			String hashed = ModuleAuth.encrypt(args[0]);

			if (hashed.equals(args[1]))
			{
				OutputHandler.chatConfirmation(sender, Localization.get("message.auth.success"));
				ModuleAuth.unLogged.remove(sender.username);
				ModuleAuth.unRegistered.remove(sender.username);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get("command.login.wrongpass"));
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get("command.login.already"));
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
