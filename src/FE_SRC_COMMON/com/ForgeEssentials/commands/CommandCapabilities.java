package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Allows you to modify a bunch of interesting stuff...
 * 
 * @author Dries007
 * 
 */

public class CommandCapabilities extends ForgeEssentialsCommandBase
{
	public static ArrayList<String> names;
	static
	{
		names = new ArrayList<String>();
		names.add("disabledamage");
		names.add("isflying");
		names.add("allowflying");
		names.add("iscreativemode");
		names.add("allowedit");
	}

	@Override
	public String getCommandName()
	{
		return "capabilities";
	}

	/*
	 * Expected syntax /capabilities [player] [capability] [value]
	 */

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length > 3)
		{
			OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
			return;
		}
		execute(sender, args);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 3)
		{
			sender.sendChatToPlayer((Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole()));
			return;
		}
		execute(sender, args);
	}

	public void execute(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendChatToPlayer("Possible capabilities:");
			for (String cap : names)
			{
				sender.sendChatToPlayer(cap);
			}
		}
		else if (args.length == 1)
		{
			EntityPlayerMP target = FunctionHelper.getPlayerFromPartialName(args[0]);
			if(PlayerSelector.hasArguments(args[0]))
			{
				target = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			sender.sendChatToPlayer(FEChatFormatCodes.GREEN + "Capabilities for " + target.username); //made this green because its a massive wall of text, and unclear whats going on
			sender.sendChatToPlayer(names.get(0) + " = " + target.capabilities.disableDamage);
			sender.sendChatToPlayer(names.get(1) + " = " + target.capabilities.isFlying);
			sender.sendChatToPlayer(names.get(2) + " = " + target.capabilities.allowFlying);
			sender.sendChatToPlayer(names.get(3) + " = " + target.capabilities.isCreativeMode);
			sender.sendChatToPlayer(names.get(4) + " = " + target.capabilities.allowEdit);
		}
		else if (args.length == 2)
		{
			EntityPlayerMP target = FunctionHelper.getPlayerFromPartialName(args[0]);
			if(PlayerSelector.hasArguments(args[0]))
			{
				target = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			if (args[1].equalsIgnoreCase(names.get(0)))
			{
				sender.sendChatToPlayer(names.get(0) + " = " + target.capabilities.disableDamage);
			}
			else if (args[1].equalsIgnoreCase(names.get(1)))
			{
				sender.sendChatToPlayer(names.get(1) + " = " + target.capabilities.isFlying);
			}
			else if (args[1].equalsIgnoreCase(names.get(2)))
			{
				sender.sendChatToPlayer(names.get(2) + " = " + target.capabilities.allowFlying);
			}
			else if (args[1].equalsIgnoreCase(names.get(3)))
			{
				sender.sendChatToPlayer(names.get(3) + " = " + target.capabilities.isCreativeMode);
			}
			else if (args[1].equalsIgnoreCase(names.get(4)))
			{
				sender.sendChatToPlayer(names.get(4) + " = " + target.capabilities.allowEdit);
			}
		}
		else if (args.length == 3)
		{
			EntityPlayerMP target = FunctionHelper.getPlayerFromPartialName(args[0]);
			if(PlayerSelector.hasArguments(args[0]))
			{
				target = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			if (args[1].equalsIgnoreCase(names.get(0)))
			{
				boolean bln = Boolean.parseBoolean(args[2]);
				target.capabilities.disableDamage = bln;
				sender.sendChatToPlayer(names.get(0) + " = " + target.capabilities.disableDamage);
			}
			else if (args[1].equalsIgnoreCase(names.get(1)))
			{
				boolean bln = Boolean.parseBoolean(args[2]);
				target.capabilities.isFlying = bln;
				sender.sendChatToPlayer(names.get(1) + " = " + target.capabilities.isFlying);
			}
			else if (args[1].equalsIgnoreCase(names.get(2)))
			{
				boolean bln = Boolean.parseBoolean(args[2]);
				target.capabilities.allowFlying = bln;
				sender.sendChatToPlayer(names.get(2) + " = " + target.capabilities.allowFlying);
			}
			else if (args[1].equalsIgnoreCase(names.get(3)))
			{
				boolean bln = Boolean.parseBoolean(args[2]);
				target.capabilities.isCreativeMode = bln;
				sender.sendChatToPlayer(names.get(3) + " = " + target.capabilities.isCreativeMode);
			}
			else if (args[1].equalsIgnoreCase(names.get(4)))
			{
				boolean bln = Boolean.parseBoolean(args[2]);
				target.capabilities.allowEdit = bln;
				sender.sendChatToPlayer(names.get(4) + " = " + target.capabilities.allowEdit);
			}

			// important!
			target.sendPlayerAbilities();
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
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else if (args.length == 2)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, names);
		}
		else if (args.length == 3)
		{
			return getListOfStringsMatchingLastWord(args, "true", "false");
		}
		else
		{
			return null;
		}
	}

}
