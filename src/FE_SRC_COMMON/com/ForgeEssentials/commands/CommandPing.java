package com.ForgeEssentials.commands;

import java.util.List;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

public class CommandPing extends FEcmdModuleCommands
{
	String	response	= "Pong! %time";

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
	public List<String> getCommandAliases()
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

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.GUESTS;
	}
}
