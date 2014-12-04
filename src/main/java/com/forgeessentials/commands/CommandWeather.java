package com.forgeessentials.commands;

import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.WeatherTimeData;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandWeather extends FEcmdModuleCommands {

	@Override
	public String getCommandName()
	{
		return "weather";
	}

	@Override
	public RegisteredPermValue getDefaultPermission()
	{
		return RegisteredPermValue.OP;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
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
			throw new WrongUsageException(getCommandUsage(sender));
		}
		WeatherTimeData wt = CommandDataManager.WTmap.get(world.provider.dimensionId);
		wt.weatherSpecified = true;

		if (args[0].equalsIgnoreCase("rain"))
		{
			if (args.length == 1)
			{
				wt.rain = !wt.rain;
			}
			else
			{
				if (args[1].equalsIgnoreCase("on"))
				{
					wt.rain = true;
					wt.storm = false;
				}
				else if (args[1].equalsIgnoreCase("off"))
				{
					wt.rain = false;
				}
				else
				{
					throw new WrongUsageException(getCommandUsage(sender));
				}
			}
			CommandDataManager.WTmap.put(wt.dimID, wt);
			return "Rain permanently turned " + (wt.rain ? "on" : "off");
		}
		else if (args[0].equalsIgnoreCase("storm"))
		{
			if (args.length == 1)
			{
				wt.storm = !wt.storm;
			}
			else
			{
				if (args[1].equalsIgnoreCase("on"))
				{
					wt.storm = true;
					wt.rain = false;
				}
				else if (args[1].equalsIgnoreCase("off"))
				{
					wt.storm = false;
				}
				else
				{
					throw new WrongUsageException(getCommandUsage(sender));
				}
			}
			CommandDataManager.WTmap.put(wt.dimID, wt);
			return "Storms permanently turned " + (wt.storm ? "on" : "off");
		}
		else if (args[0].equalsIgnoreCase("default"))
		{
			wt.weatherSpecified = false;
			CommandDataManager.WTmap.put(wt.dimID, wt);
			return "Weather reset to default";
		}
		else
		{
			throw new WrongUsageException(getCommandUsage(sender));
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/weather <rain|storm|default> [on|off] Allows for permanent manipulation of the weather.";
	}
}
