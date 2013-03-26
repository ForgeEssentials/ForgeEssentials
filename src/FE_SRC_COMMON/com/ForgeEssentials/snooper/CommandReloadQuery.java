package com.ForgeEssentials.snooper;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandReloadQuery extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "queryreload";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		reload(sender);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		reload(sender);
	}

	public void reload(ICommandSender sender)
	{
		sender.sendChatToPlayer("Killing old one....");
		ModuleSnooper.stop();
		sender.sendChatToPlayer("Making new one....");
		ModuleSnooper.start();
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.Snooper.commands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

}
