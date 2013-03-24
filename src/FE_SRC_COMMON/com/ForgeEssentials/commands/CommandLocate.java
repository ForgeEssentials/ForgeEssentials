package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandLocate extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "locate";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "gps", "loc" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length != 1)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
		else
		{
			locate(sender, args[0]);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length != 1)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
		else
		{
			locate(sender, args[0]);
		}
	}

	public void locate(ICommandSender sender, String username)
	{
		EntityPlayerMP player = PlayerSelector.matchOnePlayer(sender, username);
		if (player == null)
		{
			OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, username));
		}
		else
		{
			OutputHandler.chatConfirmation(sender, Localization.format("command.locate.msg", player.username, (int) player.posX, (int) player.posY, (int) player.posZ, player.dimension));
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
