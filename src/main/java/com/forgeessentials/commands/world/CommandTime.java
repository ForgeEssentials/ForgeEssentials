package com.forgeessentials.commands.world;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.CommandsEventHandler;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.WeatherTimeData;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class CommandTime extends FEcmdModuleCommands {
	@Override
	public String getCommandName()
	{
		return "time";
	}

	@Override
	public RegisteredPermValue getDefaultPermission()
	{
		return RegisteredPermValue.OP;
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP sender, String[] args)
	{
		if (args.length != 0 && FunctionHelper.isNumeric(args[0]))
		{
			String[] newArgs = new String[args.length - 1];
			for (int i = 0; i < args.length - 1; i++)
			{
				newArgs[i] = args[i + 1];
			}
			String msg = doCmd(sender, DimensionManager.getWorld(parseInt(sender, args[0])), newArgs);
			if (msg != null)
			{
				OutputHandler.chatConfirmation(sender, msg);
			}
		}
		else
		{
			String msg = null;
			for (World world : DimensionManager.getWorlds())
			{
				msg = doCmd(sender, world, args);
			}
			if (msg != null)
			{
				OutputHandler.chatConfirmation(sender, msg);
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length != 0 && FunctionHelper.isNumeric(args[0]))
		{
			String[] newArgs = new String[args.length - 1];
			for (int i = 0; i < args.length - 1; i++)
			{
				newArgs[i] = args[i + 1];
			}
			String msg = doCmd(sender, DimensionManager.getWorld(parseInt(sender, args[0])), newArgs);
			if (msg != null)
			{
				OutputHandler.chatConfirmation(sender, msg);
			}
		}
		else
		{
			String msg = null;
			for (World world : DimensionManager.getWorlds())
			{
				msg = doCmd(sender, world, args);
			}
			if (msg != null)
			{
				OutputHandler.chatConfirmation(sender, msg);
			}
		}
	}

	public String doCmd(ICommandSender sender, World world, String[] args)
	{
		if (args.length == 0)
		{
			throw new TranslatedCommandException(getCommandUsage(sender));
		}

		switch (args[0]) {
		case "set":
		{
			if (args[1].equalsIgnoreCase("day")) {
				CommandsEventHandler.makeWorldTimeHours(world, WeatherTimeData.dayTimeStart);
			} else if (args[1].equalsIgnoreCase("night")) {
				CommandsEventHandler.makeWorldTimeHours(world, WeatherTimeData.nightTimeStart);
			} else {
				world.setWorldTime(parseInt(sender, args[1]));
			}
            WeatherTimeData wt = CommandDataManager.WTmap.get(world.provider.dimensionId);
            wt.freezeTime = world.getWorldTime();
			return Translator.format("Set time to %s.", args[1]);
		}
		case "add":
		{
			if (args.length == 1) {
				throw new TranslatedCommandException("Improper syntax. Please try this instead: [dimID, none for all] <freeze|lock|set|add> <time (number)|day|night>");
			}
			world.setWorldTime(world.getWorldTime() + parseInt(sender, args[1]));
            WeatherTimeData wt = CommandDataManager.WTmap.get(world.provider.dimensionId);
            wt.freezeTime = world.getWorldTime();
			return Translator.format("Added %d to the current time.", args[1]);

		}
		case "freeze":
		{
			WeatherTimeData wt = CommandDataManager.WTmap.get(world.provider.dimensionId);
			wt.freezeTime = world.getWorldTime();
			wt.timeFreeze = !wt.timeFreeze;
			return "Time freeze" + (wt.timeFreeze ? "on" : "off");
		}
		case "lock":
		{
			WeatherTimeData wt = CommandDataManager.WTmap.get(world.provider.dimensionId);
			if (args.length == 1) {
				wt.timeSpecified = !wt.timeSpecified;
			} else {
				wt.timeSpecified = true;
				if (args[1].equalsIgnoreCase("day")) {
					wt.day = true;
				} else if (args[1].equalsIgnoreCase("night")) {
					wt.day = false;
				} else {
					throw new TranslatedCommandException("Improper syntax. Please try this instead: [dimID, none for all] <freeze|lock|set|add> <time (number)|day|night>");
				}
			}
			return Translator.format("Locked time to %s.", args[1]);
		}
		default:
		{
			throw new TranslatedCommandException(getCommandUsage(sender));
		}
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

    @Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "freeze", "set", "add", "lock");
		}
		if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("lock"))
		{
			return getListOfStringsMatchingLastWord(args, "day", "night");
		}
		return null;
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/time [dimID, none for all] <freeze|lock|set|add> <time (number)|day|night> Manipulate time.";
	}
}