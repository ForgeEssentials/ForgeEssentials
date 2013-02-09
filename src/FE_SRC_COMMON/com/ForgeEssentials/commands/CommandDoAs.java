package com.ForgeEssentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandDoAs extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "doas";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		StringBuilder cmd = new StringBuilder(args.toString().length());
		for (int i = 1; i < args.length; i++)
		{
			cmd.append(args[i]);
			cmd.append(" ");
		}
		EntityPlayer target = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
		target.sendChatToPlayer("Player " + sender.username + "is attempting to issue a command as you.");// hook
																									// into
																									// questioner
		FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(target, cmd.toString());
		OutputHandler.chatConfirmation(sender, "Successfully issued command as " + args[0]);

	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		StringBuilder cmd = new StringBuilder(args.toString().length());
		for (int i = 1; i < args.length; i++)
		{
			cmd.append(args[i]);
			cmd.append(" ");
		}
		EntityPlayer target = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
		if (PlayerSelector.hasArguments(args[0]))
		{
			target = PlayerSelector.matchOnePlayer(sender, args[0]);
		}
		String senderName = (sender instanceof TileEntityCommandBlock ? "CommandBlock @ (" + ((TileEntityCommandBlock) sender).xCoord + "," + ((TileEntityCommandBlock) sender).yCoord + "," + ((TileEntityCommandBlock) sender).zCoord + ")."
				: "The console");
		target.sendChatToPlayer(senderName + " is attempting to issue a command as you.");// hook
																							// into
																							// questioner
		FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(target, cmd.toString());// Problem
																															// is,
																															// things
																															// like
																															// motd
																															// go
																															// to
																															// the
																															// player.
		if (!(sender instanceof TileEntityCommandBlock))
		{
			sender.sendChatToPlayer("Successfully issued command as " + args[0]);
		}
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
