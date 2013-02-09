package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.core.misc.LoginMessage;

public class CommandMotd extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "motd";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		LoginMessage.sendLoginMessage(sender);
		if(args.length > 0 && args[0].equalsIgnoreCase("reload"))
		{
			LoginMessage.loadFile();
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		LoginMessage.sendLoginMessage(sender);
		if(args.length > 0 && args[0].equalsIgnoreCase("reload"))
		{
			LoginMessage.loadFile();
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
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "reload");
		}
		else
		{
			return null;
		}
	}
}
