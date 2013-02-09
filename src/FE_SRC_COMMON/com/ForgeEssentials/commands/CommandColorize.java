package com.ForgeEssentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

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
		sender.sendChatToPlayer("Click a sign to colorize!");
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		// NOOP
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
}
