package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandPing extends ForgeEssentialsCommandBase
{
	String response = "Pong! %time";
	
	@Override
	public void doConfig(Configuration config, String category)
	{
		response = config.get(category, "response", "Pong! %time").value;
	}
	
	@Override
	public String getCommandName()
	{
		return "ping";
	}

	@Override
	public List getCommandAliases()
	{
		return null;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		sender.sendChatToPlayer(response.replaceAll("%time", ((EntityPlayerMP) sender).ping + "ms."));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		sender.sendChatToPlayer(response.replaceAll("%time", ""));
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
