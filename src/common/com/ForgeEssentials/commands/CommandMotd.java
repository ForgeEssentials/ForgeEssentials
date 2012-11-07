package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.FEConfig;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

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
		if (args.length > 0)
		{
			ForgeEssentials.instance.config.changeConfig("Basic", "motd", args[0]);
			OutputHandler.SOP("MOTD changed to " + FEConfig.motd);
		} else
			player.addChatMessage(FEConfig.motd);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			ForgeEssentials.instance.config.changeConfig("Basic", "motd", args[0]);
			OutputHandler.SOP("MOTD changed to " + FEConfig.motd);
		} else
			OutputHandler.SOP(FEConfig.motd);
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
	
	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO: check permissions
		return true;
	}
}
