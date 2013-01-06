package com.ForgeEssentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.OutputHandler;

public class CommandMotd extends ForgeEssentialsCommandBase
{

	public static String motd;

	@Override
	public String getCommandName()
	{
		return "motd";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length > 0)
		{
			motd = "";
			for (String arg : args)
			{
				motd = motd + arg + " ";
			}
			ForgeEssentials.config
					.changeProperty("Miscellaneous", "motd", motd);
			OutputHandler
					.chatConfirmation(sender, "MOTD successfully changed.");
		} else
		{
			sender.sendChatToPlayer(motd);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			motd = "";
			for (String arg : args)
			{
				motd = motd + arg + " ";
			}
			ForgeEssentials.config
					.changeProperty("Miscellaneous", "motd", motd);
			OutputHandler.SOP("MOTD successfully changed");
		} else
		{
			sender.sendChatToPlayer(motd);
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
