package com.forgeessentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandDoAs extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "doas";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		StringBuilder cmd = new StringBuilder(args.toString().length());
		for (int i = 1; i < args.length; i++)
		{
			cmd.append(args[i]);
			cmd.append(" ");
		}
		EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
		if (player != null)
		{
			OutputHandler.chatWarning(player, String.format("Player %s is attempting to issue a command as you.", sender.getCommandSenderName()));
			FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(player, cmd.toString());
			OutputHandler.chatConfirmation(sender, String.format("Successfully issued command as %s", args[0]));
		}
		else
		{
			OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
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

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/doas <player> <command> Run a command as another player.";
	}
}
