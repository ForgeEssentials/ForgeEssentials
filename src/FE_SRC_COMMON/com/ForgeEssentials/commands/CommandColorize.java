package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandColorize extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "colorize";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		sender.getEntityData().setBoolean("colorize", true);
		OutputHandler.chatConfirmation(sender, Localization.get("command.colorize.msg"));
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
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}
}
