package com.forgeessentials.worldborder;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandFiller extends ForgeEssentialsCommandBase {
    // Zone name - filler
    public static HashMap<Integer, TickTaskFill> map = new HashMap<Integer, TickTaskFill>();

    @Override
    public String getCommandName()
    {
        return "filler";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            return;
        }

        execute(sender, args);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            return;
        }

        execute(sender, args);
    }

    private void execute(ICommandSender sender, String[] args)
    {
        /*
         * No world, Status update.
		 */
        if (args.length == 0)
        {
            // Header
            String header = "--- Fillers active ---";
            ChatUtils.sendMessage(sender, header);
            // Actual info
            for (Integer world : map.keySet())
            {
                ChatUtils.sendMessage(sender, world + ": " + map.get(world).getStatus());
            }
            // Footer
            StringBuilder footer = new StringBuilder();
            for (int i = 3; i < header.length(); i++)
            {
                footer.append("-");
            }
            ChatUtils.sendMessage(sender, footer.toString());
            return;
        }

		/*
         * Get the world
		 */
        WorldServer world = null;
        if (FunctionHelper.isNumeric(args[0]))
        {
            world = DimensionManager.getWorld(parseInt(sender, args[0]));
        }
        else if ((args[0].equalsIgnoreCase("here") || args[0].equalsIgnoreCase("world")) && sender instanceof EntityPlayer)
        {
            world = (WorldServer) ((EntityPlayer) sender).worldObj;
        }

        if (world == null)
        {
            OutputHandler.chatError(sender, args[0] + " is not an ID of a loaded world.");
            return;
        }

        if (args.length == 1)
        {
            if (map.containsKey(world.provider.dimensionId))
            {
                OutputHandler.chatConfirmation(sender, map.get(world.provider.dimensionId).getStatus());
            }
            else
            {
                OutputHandler.chatError(sender, "There is no filler running for that world.");
            }
        }
        else
        {
            if (args[1].equalsIgnoreCase("start"))
            {
                if (!map.containsKey(world.provider.dimensionId))
                {
                    map.put(world.provider.dimensionId, new TickTaskFill(world, sender, true));
                }
                else
                {
                    OutputHandler.chatError(sender, "Filler already running for that world!");
                }
            }
            else if (args[1].equalsIgnoreCase("stop"))
            {
                if (!map.containsKey(world.provider.dimensionId))
                {
                    OutputHandler.chatError(sender, "There is no filler running for that world.");
                }
                else
                {
                    map.get(world.provider.dimensionId).stop();
                }
            }
            else if (args[1].equalsIgnoreCase("reset"))
            {
                if (!map.containsKey(world.provider.dimensionId))
                {
                    map.put(world.provider.dimensionId, new TickTaskFill(world, sender, false));
                }
                else
                {
                    OutputHandler.chatError(sender, "Filler already running for that world!");
                }
            }
            else if (args[1].equalsIgnoreCase("speed"))
            {
                if (!map.containsKey(world.provider.dimensionId))
                {
                    OutputHandler.chatError(sender, "There is no filler running for that world.");
                }
                else
                {
                    if (args.length != 3)
                    {
                        OutputHandler.chatConfirmation(sender, "Current speed: " + map.get(world.provider.dimensionId));
                    }
                    else
                    {
                        map.get(world.provider.dimensionId).speed(parseIntWithMin(sender, args[2], 0));
                        if (map.get(world.provider.dimensionId).source != sender)
                        {
                            OutputHandler.chatError(sender, "Changed speed to " + args[2]);
                        }
                    }
                }
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
        // Zone selection
        if (args.length == 1)
        {
            ArrayList<String> list = new ArrayList<String>();
            if (sender instanceof EntityPlayer)
            {
                list.add("world");
                list.add("here");
            }
            for (int i : DimensionManager.getIDs())
            {
                list.add("" + i);
            }
            return getListOfStringsFromIterableMatchingLastWord(args, list);
        }
        if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, "start", "stop", "reset", "speed");
        }
        return null;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.worldborder.filler";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return null;
    }

    @Override
    public RegGroup getReggroup()
    {

        return RegGroup.OWNERS;
    }

}
