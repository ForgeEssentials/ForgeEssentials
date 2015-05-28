package com.forgeessentials.commands.world;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.CommandButcherTickTask;
import com.forgeessentials.commands.util.CommandButcherTickTask.ButcherMobType;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandButcher extends FEcmdModuleCommands
{

    public static List<String> typeList = ButcherMobType.getNames();

    @Override
    public String getCommandName()
    {
        return "febutcher";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "butcher" };
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
            return getListOfStringsMatchingLastWord(args, typeList);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        int radius = -1;
        double x = sender.posX;
        double y = sender.posY;
        double z = sender.posZ;
        World world = sender.worldObj;
        String mobType = ButcherMobType.HOSTILE.toString();

        Queue<String> argsStack = new LinkedList<String>(Arrays.asList(args));
        if (!argsStack.isEmpty())
        {
            String radiusValue = argsStack.remove();
            if (radiusValue.equalsIgnoreCase("world"))
                radius = -1;
            else
                radius = parseIntWithMin(sender, radiusValue, 0);
        }

        if (!argsStack.isEmpty())
            mobType = argsStack.remove();

        if (!argsStack.isEmpty())
        {
            if (argsStack.size() < 3)
                throw new TranslatedCommandException("Improper syntax: <radius> [type] [x y z] [world]");
            x = parseDouble(sender, argsStack.remove(), sender.posX);
            y = parseDouble(sender, argsStack.remove(), sender.posY);
            z = parseDouble(sender, argsStack.remove(), sender.posZ);
        }

        if (!argsStack.isEmpty())
        {
            world = DimensionManager.getWorld(parseInt(sender, argsStack.remove()));
            if (world == null)
                throw new TranslatedCommandException("The specified dimension does not exist");
        }

        AxisAlignedBB pool = AxisAlignedBB.getBoundingBox(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1, z + radius + 1);
        CommandButcherTickTask.schedule(sender, world, mobType, pool, radius);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        int radius = -1;
        double x = 0;
        double y = 0;
        double z = 0;
        World world = DimensionManager.getWorld(0);
        String mobType = ButcherMobType.HOSTILE.toString();

        Queue<String> argsStack = new LinkedList<String>(Arrays.asList(args));

        if (!argsStack.isEmpty())
        {
            String radiusValue = argsStack.remove();
            if (radiusValue.equalsIgnoreCase("world"))
                radius = -1;
            else
                radius = parseIntWithMin(sender, radiusValue, 0);
        }

        if (!argsStack.isEmpty())
            mobType = argsStack.remove();

        if (!argsStack.isEmpty())
        {
            if (argsStack.size() < 3)
                throw new TranslatedCommandException(getCommandUsage(sender));
            x = parseInt(sender, argsStack.remove());
            y = parseInt(sender, argsStack.remove());
            z = parseInt(sender, argsStack.remove());
        }
        else
        {
            if (sender instanceof CommandBlockLogic)
            {
                CommandBlockLogic cb = (CommandBlockLogic) sender;
                world = cb.getEntityWorld();
                ChunkCoordinates coords = cb.getPlayerCoordinates();
                x = coords.posX;
                y = coords.posY;
                z = coords.posZ;
            }
            else
                throw new TranslatedCommandException(getCommandUsage(sender));
        }

        if (!argsStack.isEmpty())
        {
            world = DimensionManager.getWorld(parseInt(sender, argsStack.remove()));
            if (world == null)
                throw new TranslatedCommandException("This dimension does not exist");
        }
        AxisAlignedBB pool = AxisAlignedBB.getBoundingBox(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1, z + radius + 1);
        CommandButcherTickTask.schedule(sender, world, mobType, pool, radius);
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
