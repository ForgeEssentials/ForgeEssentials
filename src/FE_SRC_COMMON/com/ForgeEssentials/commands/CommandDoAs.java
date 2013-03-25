package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandDoAs extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "doas";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		ex(sender, args);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		ex(sender, args);
	}

	private void ex(ICommandSender sender, String[] args)
	{
		StringBuilder cmd = new StringBuilder(args.toString().length());
		for (int i = 1; i < args.length; i++)
		{
			cmd.append(args[i]);
			cmd.append(" ");
		}
		EntityPlayerMP player = FunctionHelper.getPlayerForName(args[0]);
		if (player != null)
		{
			OutputHandler.chatWarning(player, Localization.format("command.doas.attempt", sender.getCommandSenderName()));
			FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(player, cmd.toString());
			OutputHandler.chatConfirmation(sender, Localization.format("command.doas.success", args[0]));
		}
		else
		{
			OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
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
}
