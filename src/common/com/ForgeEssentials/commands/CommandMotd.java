package com.ForgeEssentials.commands;

import com.ForgeEssentials.ForgeEssentials;
import com.ForgeEssentials.OutputHandler;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.TileEntityCommandBlock;

public class CommandMotd extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "MOTD";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		player.addChatMessage(ForgeEssentials.motd);
	}

	@Override
	public void processCommandBlock(TileEntityCommandBlock block, String[] args)
	{
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		OutputHandler.SOP(ForgeEssentials.motd);
	}

	@Override
	public String getUsageConsole()
	{
		return "/motd [new MOTD] Get the Message Of The Day";
	}

	@Override
	public String getUsageCommandBlock(TileEntityCommandBlock block)
	{
		return null;
	}

	@Override
	public String getUsagePlayer(EntityPlayer player)
	{
		return "/motd [new MOTD] Get the Message Of The Day";
	}
}
