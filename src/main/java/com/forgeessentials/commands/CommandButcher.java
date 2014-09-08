package com.forgeessentials.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.EnumMobType;
import com.forgeessentials.commands.util.CommandButcherTickTask;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.selections.WorldPoint;
import com.forgeessentials.util.tasks.TaskRegistry;

public class CommandButcher extends FEcmdModuleCommands {
    public static List<String> typeList = new ArrayList<String>();

    static
    {
        for (EnumMobType type : EnumMobType.values())
        {
            typeList.add(type.name());
        }
    }

    @Override
    public String getCommandName()
    {
        return "butcher";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "-1");
        }
        else if (args.length == 2)
        {
            return getListOfStringsFromIterableMatchingLastWord(args, typeList);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        int radius = -1;
        double X = sender.posX;
        double Y = sender.posY;
        double Z = sender.posZ;
        String mobType = EnumMobType.HOSTILE.toString();

        if (args.length > 0)
        {
            radius = args[0].equalsIgnoreCase("world") ? -1 : parseIntWithMin(sender, args[0], 0);
        }
        if (args.length > 1)
        {
            if (typeList.contains(args[1]))
            {
                mobType = args[1];
            }
            else
            {
                OutputHandler.chatError(sender, "Improper syntax. Please try this instead: [radius|-1|world] [type] [x, y, z]");
                return;
            }
        }
        if (args.length > 2)
        {
            String splitter = "";
            if (args[2].contains(", "))
            {
                splitter = ", ";
            }
            else if (args[2].contains(","))
            {
                splitter = ",";
            }
            else if (args[2].contains(" "))
            {
                splitter = " ";
            }

            String[] split = args[2].split(splitter);
            if (split.length != 3)
            {
                OutputHandler.chatError(sender, "Improper syntax. Please try this instead: " + "x, y, z");
                return;
            }
            else
            {
                X = parseDouble(sender, split[0], sender.posX);
                Y = parseDouble(sender, split[1], sender.posY);
                Z = parseDouble(sender, split[2], sender.posZ);
            }
        }
        AxisAlignedBB pool = AxisAlignedBB.getBoundingBox(X - radius, Y - radius, Z - radius, X + radius + 1, Y + radius + 1, Z + radius + 1);
        TaskRegistry.registerTask(new CommandButcherTickTask(sender, mobType, pool, radius, sender.dimension));
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        int radius = -1;
        int worldID = 0;
        int x = 0, y = 0, z = 0;

        if (sender instanceof TileEntityCommandBlock)
        {
            TileEntityCommandBlock cb = (TileEntityCommandBlock) sender;
            worldID = cb.getWorldObj().provider.dimensionId;
            x = cb.xCoord;
            y = cb.yCoord;
            z = cb.zCoord;
        }

        String mobType = "hostile";

        if (args.length != 4 && !(sender instanceof TileEntityCommandBlock))
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: &lt;radius|-1|world> &lt;type> &lt;x, y, z> &lt;dimID>");
            return;
        }
        if (args.length > 0)
        {
            radius = args[0].equalsIgnoreCase("world") ? -1 : parseIntWithMin(sender, args[0], 0);
        }
        if (args.length > 1)
        {
            if (typeList.contains(args[1]))
            {
                mobType = args[1];
            }
            else
            {
                OutputHandler.chatError(sender, "Improper syntax. Please try this instead: " + FunctionHelper.niceJoin(typeList.toArray()));
                return;
            }
        }
        if (args.length > 2)
        {
            String splitter = "";
            if (args[2].contains(", "))
            {
                splitter = ", ";
            }
            else if (args[2].contains(","))
            {
                splitter = ",";
            }
            else if (args[2].contains(" "))
            {
                splitter = " ";
            }

            String[] split = args[2].split(splitter);
            if (split.length != 3)
            {
                OutputHandler.chatError(sender, "Improper syntax. Please try this instead: " + "x, y, z");
                return;
            }
            else
            {
                x = parseInt(sender, split[0]);
                y = parseInt(sender, split[1]);
                z = parseInt(sender, split[2]);
            }
        }
		if (args.length == 4) 
		{
			worldID = parseInt(sender, args[3]);
		}
        WorldPoint center = new WorldPoint(worldID, x, y, z);
        AxisAlignedBB pool = AxisAlignedBB.getBoundingBox(center.x - radius, center.y - radius, center.z - radius, center.x + radius + 1, center.y + radius + 1,
                center.z + radius + 1);
        TaskRegistry.registerTask(new CommandButcherTickTask(sender, mobType, pool, radius, worldID));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/butcher [radius|-1|world] [type] [x, y, z] Kills the type of mobs within the specified radius around the specified point in the specified world.";
    }

}
