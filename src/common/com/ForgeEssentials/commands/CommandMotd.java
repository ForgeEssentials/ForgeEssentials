package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.FEConfig;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.OutputHandler;

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

		player.addChatMessage(FEConfig.instance.motd);

		if (args.length > 0)
		{
			FEConfig.instance.changeConfig("Basic", "motd", args[0]);
			OutputHandler.SOP("MOTD changed to " + FEConfig.instance.motd);
		} else
			player.addChatMessage(FEConfig.instance.motd);

	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		OutputHandler.SOP(FEConfig.instance.motd);

		if (args.length > 0)
		{
			FEConfig.instance.changeConfig("Basic", "motd", args[0]);
			OutputHandler.SOP("MOTD changed to " + FEConfig.instance.motd);
		} else
			OutputHandler.SOP(FEConfig.instance.motd);

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
