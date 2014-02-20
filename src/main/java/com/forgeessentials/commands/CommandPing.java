package com.forgeessentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;

public class CommandPing extends FEcmdModuleCommands
{
	String	response	= "Pong! %time";

	@Override
	public void doConfig(Configuration config, String category)
	{
		response = config.get(category, "response", "Pong! %time").getString();
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
		ChatUtils.sendMessage(sender, response.replaceAll("%time", ((EntityPlayerMP) sender).ping + "ms."));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		ChatUtils.sendMessage(sender, response.replaceAll("%time", ""));
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
