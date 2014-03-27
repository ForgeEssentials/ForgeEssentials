package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.WeatherTimeData;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class CommandWeather extends FEcmdModuleCommands
{

    @Override
    public String getCommandName()
    {
        return "weather";
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length != 0 && FunctionHelper.isNumeric(args[0]))
        {
            try
            {
                String[] newArgs = new String[args.length - 1];
                for (int i = 0; i < args.length - 1; i++)
                    newArgs[i] = args[i + 1];
                String msg = doCmd(sender, DimensionManager.getWorld(parseInt(sender, args[0])), newArgs);
                if(msg != null) OutputHandler.chatConfirmation(sender, msg);    
            }
            catch (Exception e)
            {
            }
        }
        else
        {
            String msg = null;
            for (World world : DimensionManager.getWorlds())
                try
                {
                    msg = doCmd(sender, world, args);
                }
                catch (Exception e)
                {
                    break;
                }
            if(msg != null) OutputHandler.chatConfirmation(sender, msg);
        }
    }
    
    public String doCmd(ICommandSender sender, World world, String[] args) throws Exception
    {
        if (args.length == 0)
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: " + getSyntaxConsole());
            throw new Exception();
        }
        WeatherTimeData wt = CommandDataManager.WTmap.get(world.provider.dimensionId);
        wt.weatherSpecified = true;
        
        if (args[0].equalsIgnoreCase("rain"))
        {
            if (args.length == 1) wt.rain = !wt.rain;
            else
            {
                if (args[1].equalsIgnoreCase("on")) wt.rain = true;
                else if (args[1].equalsIgnoreCase("off")) wt.rain = false;
                else
                {
                    OutputHandler.chatError(sender, "Improper syntax. Please try this instead: " + getSyntaxConsole());
                    throw new Exception();
                }
            }
            CommandDataManager.WTmap.put(wt.dimID, wt);
            return Localization.get("command.weather.rain." + (wt.rain ? "on" : "off"));
        }
        else if (args[0].equalsIgnoreCase("storm"))
        {
            if (args.length == 1) wt.storm = !wt.storm;
            else
            {
                if (args[1].equalsIgnoreCase("on")) wt.storm = true;
                else if (args[1].equalsIgnoreCase("off")) wt.storm = false;
                else
                {
                    OutputHandler.chatError(sender, "Improper syntax. Please try this instead: " + getSyntaxConsole());
                    throw new Exception();
                }
            }
            CommandDataManager.WTmap.put(wt.dimID, wt);
            return Localization.get("command.weather.storm." + (wt.storm ? "on" : "off"));
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: " + getSyntaxConsole());
            throw new Exception();
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
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
