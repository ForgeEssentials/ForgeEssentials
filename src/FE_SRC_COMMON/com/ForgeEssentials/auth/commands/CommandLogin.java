package com.ForgeEssentials.auth.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.auth.ModuleAuth;
import com.ForgeEssentials.auth.pwdData;
import com.ForgeEssentials.auth.pwdSaver;
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
		else
		{
			if (pwdSaver.isRegisted(sender.username))
			{
				try
				{
					pwdData data = pwdSaver.getData(sender.username);
					if (ModuleAuth.pwdEnc.authenticate(args[0], data.encPwd, data.salt))
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
				catch (Exception e)
				{
					OutputHandler.chatError(sender, e.toString());
					e.printStackTrace();
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get("command.login.register"));
			}
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
