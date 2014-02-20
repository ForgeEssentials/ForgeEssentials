package com.forgeessentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.TickHandlerCommands;
import com.forgeessentials.commands.util.WeatherTimeData;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;

public class CommandTime extends FEcmdModuleCommands
{
    @Override
    public String getCommandName()
    {
        return "time";
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
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

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
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
            OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
            throw new Exception();
        }
        if (args[0].equalsIgnoreCase("freeze"))
        {
            WeatherTimeData wt = CommandDataManager.WTmap.get(world.provider.dimensionId);
            wt.freezeTime = world.getWorldTime();
            wt.timeFreeze = !wt.timeFreeze;
            CommandDataManager.WTmap.put(wt.dimID, wt);
            return Localization.get("command.time.freeze." + (wt.timeFreeze ? "on" : "off"));
        }
        else if (args[0].equalsIgnoreCase("lock"))
        {
            WeatherTimeData wt = CommandDataManager.WTmap.get(world.provider.dimensionId);
            if (args.length == 1)
            {
                wt.timeSpecified = !wt.timeSpecified;
            }
            else
            {
                wt.timeSpecified = true;
                if (args[1].equalsIgnoreCase("day")) wt.day = true;
                else if (args[1].equalsIgnoreCase("night")) wt.day = false;
                else
                {
                    OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
                    throw new Exception();
                }
            }
            CommandDataManager.WTmap.put(wt.dimID, wt);
            return Localization.format("command.time.lock", args[1]);
        }
        else if (args[0].equalsIgnoreCase("set"))
        {
            if (args[1].equalsIgnoreCase("day"))
                TickHandlerCommands.makeWorldTimeHours(world, WeatherTimeData.dayTimeStart);
            else if (args[1].equalsIgnoreCase("night"))
                TickHandlerCommands.makeWorldTimeHours(world, WeatherTimeData.nightTimeStart);
            else
                world.setWorldTime(parseInt(sender, args[1]));
            
            return Localization.format("command.time.set", args[1]);
        }
        else if (args[0].equalsIgnoreCase("add"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
                throw new Exception();
            }
            world.setWorldTime(world.getWorldTime() + parseInt(sender, args[1]));
            return Localization.format("command.time.add", args[1]);
        }
        else
        {
            OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
            throw new Exception();
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "freeze", "set", "add", "lock");
        if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("lock"))
            return getListOfStringsMatchingLastWord(args, "day", "night");
        return null;
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