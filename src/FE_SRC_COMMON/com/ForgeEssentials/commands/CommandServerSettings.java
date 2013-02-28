package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.dedicated.DedicatedServer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandServerSettings extends ForgeEssentialsCommandBase
{
	public static List<String>	options	= Arrays.asList("allowFlight", "allowPVP", "buildLimit", "difficulty", "MOTD", "onlineMode", "spawnProtection");

	@Override
	public String getCommandName()
	{
		return "serversettings";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "ss" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		doStuff(sender, args);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		doStuff(sender, args);
	}

	public void doStuff(ICommandSender sender, String[] args)
	{
		if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
			return;
		DedicatedServer server = (DedicatedServer) FMLCommonHandler.instance().getMinecraftServerInstance();
		if (args.length == 0)
		{
			sender.sendChatToPlayer("Available options:");
			sender.sendChatToPlayer(options.toString());
			return;
		}

		if (args[0].equalsIgnoreCase("allowFlight"))
		{
			if (args.length == 1)
			{
				OutputHandler.chatConfirmation(sender, "allowFlight: " + server.isFlightAllowed());
			}
			else
			{
				server.setAllowFlight(Boolean.parseBoolean(args[1]));
				server.setProperty("allow-flight", Boolean.parseBoolean(args[1]));
				server.saveProperties();
				OutputHandler.chatConfirmation(sender, "allowFlight: " + server.isFlightAllowed());
			}
			return;
		}

		if (args[0].equalsIgnoreCase("allowPVP"))
		{
			if (args.length == 1)
			{
				OutputHandler.chatConfirmation(sender, "allowPVP: " + server.isPVPEnabled());
			}
			else
			{
				server.setAllowPvp(Boolean.parseBoolean(args[1]));
				server.setProperty("pvp", Boolean.parseBoolean(args[1]));
				server.saveProperties();
				OutputHandler.chatConfirmation(sender, "allowPVP: " + server.isPVPEnabled());
			}
			return;
		}

		if (args[0].equalsIgnoreCase("buildLimit"))
		{
			if (args.length == 1)
			{
				OutputHandler.chatConfirmation(sender, "buildLimit: " + server.getBuildLimit());
			}
			else
			{
				server.setBuildLimit(parseIntWithMin(sender, args[1], 0));
				server.setProperty("max-build-height", parseIntWithMin(sender, args[1], 0));
				server.saveProperties();
				OutputHandler.chatConfirmation(sender, "buildLimit: " + server.getBuildLimit());
			}
			return;
		}

		if (args[0].equalsIgnoreCase("MOTD"))
		{
			if (args.length == 1)
			{
				OutputHandler.chatConfirmation(sender, "MOTD: " + server.getMOTD());
			}
			else
			{
				String msg = "";
				for (String var : FunctionHelper.dropFirstString(args))
				{
					msg += " " + var;
				}
				server.setMOTD(msg.substring(1));
				server.setProperty("motd", msg.substring(1));
				server.saveProperties();
				OutputHandler.chatConfirmation(sender, "MOTD: " + server.getMOTD());
			}
			return;
		}

		if (args[0].equalsIgnoreCase("spawnProtection"))
		{
			if (args.length == 1)
			{
				OutputHandler.chatConfirmation(sender, "spawnProtection: " + server.getSpawnProtectionSize());
			}
			else
			{
				server.setProperty("spawn-protection", parseIntWithMin(sender, args[1], 0));
				server.saveProperties();
				OutputHandler.chatConfirmation(sender, "spawnProtection: " + server.getSpawnProtectionSize());
			}
			return;
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
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsFromIterableMatchingLastWord(args, options);
		else
			return null;
	}
}
