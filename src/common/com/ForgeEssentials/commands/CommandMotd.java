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
		return "motd";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (args.length > 0)
		{
			FEConfig.changeConfig("Basic", "motd", args[0]);
			OutputHandler.SOP("MOTD changed to " + FEConfig.getMotd());
		} else
			player.addChatMessage(FEConfig.getMotd());
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			FEConfig.changeConfig("Basic", "motd", args[0]);
			OutputHandler.SOP("MOTD changed to " + FEConfig.getMotd());
		} else
			OutputHandler.SOP(FEConfig.getMotd());
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

	@Override
	public String getSyntaxConsole()
	{
		return "/motd [new MOTD]";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/motd [new MOTD]";
	}

	@Override
	public String getInfoConsole()
	{
		return "Get/set the Message Of the Day";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Get/set the Message Of the Day";
	}
}
