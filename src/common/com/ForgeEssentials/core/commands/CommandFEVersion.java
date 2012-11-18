package com.ForgeEssentials.core.commands;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.Version;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

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
		player.addChatMessage("You are currently running ForgeEssentials version " + Version.version);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		sender.sendChatToPlayer("You are currently running ForgeEssentials version " + Version.version);
	}

	@Override
	public String getSyntaxConsole()
	{
		return "/feversion";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/feversion";
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
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
}