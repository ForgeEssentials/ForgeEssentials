package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.core.misc.LoginMessage;

public class CommandMotd extends FEcmdModuleCommands
{

	@Override
	public String getCommandName()
	{
		return "motd";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length > 0 && args[0].equalsIgnoreCase("reload"))
		{
			LoginMessage.loadFile();
		}
		LoginMessage.sendLoginMessage(sender);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 0 && args[0].equalsIgnoreCase("reload"))
		{
			LoginMessage.loadFile();
		}
		LoginMessage.sendLoginMessage(sender);
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "reload");
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.GUESTS;
	}
}
