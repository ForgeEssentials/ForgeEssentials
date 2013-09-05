package com.ForgeEssentials.core.commands;

import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.preloader.FEModContainer;

public class CommandFEVersion extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "feversion";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		player.addChatMessage("You are currently running ForgeEssentials version " + FEModContainer.version);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		ChatUtils.sendMessage(sender, "You are currently running ForgeEssentials version " + FEModContainer.version);
	}

	@Override
	public String getSyntaxConsole()
	{
		return "";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "";
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getInfoConsole()
	{
		return "Get the current version of ForgeEssentials";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Get the current version of ForgeEssentials";
	}

	@Override
	public String getCommandPerm()
	{
		return null;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}
}
