package com.forgeessentials.commands.world;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.CommandButcherTickTask;
import com.forgeessentials.commands.util.CommandButcherTickTask.ButcherMobType;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandButcher extends ForgeEssentialsCommandBase
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
    public String getCommandUsage(ICommandSender sender)
    {
        return "/butcher [radius|-1|world] [type] [x, y, z] Kills the type of mobs within the specified radius around the specified point in the specified world.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".butcher";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args, BlockPos pos)
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
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        int radius = -1;
        double x = sender.posX;
        double y = sender.posY;
        double z = sender.posZ;
        World world = sender.worldObj;
        String mobType = ButcherMobType.HOSTILE.toString();

        Queue<String> argsStack = new LinkedList<>(Arrays.asList(args));
        if (!argsStack.isEmpty())
        {
            String radiusValue = argsStack.remove();
            if (radiusValue.equalsIgnoreCase("world"))
                radius = -1;
            else
                radius = parseInt(radiusValue, -1);
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
            world = DimensionManager.getWorld(parseInt(argsStack.remove()));
            if (world == null)
                throw new TranslatedCommandException("The specified dimension does not exist");
        }

        AxisAlignedBB pool = AxisAlignedBB.fromBounds(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1, z + radius + 1);
        CommandButcherTickTask.schedule(sender, world, mobType, pool, radius);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        int radius = -1;
        double x = 0;
        double y = 0;
        double z = 0;
        World world = DimensionManager.getWorld(0);
        String mobType = ButcherMobType.HOSTILE.toString();

        Queue<String> argsStack = new LinkedList<>(Arrays.asList(args));

        if (!argsStack.isEmpty())
        {
            String radiusValue = argsStack.remove();
            if (radiusValue.equalsIgnoreCase("world"))
                radius = -1;
            else
                radius = parseInt(radiusValue, 0);
        }

        if (!argsStack.isEmpty())
            mobType = argsStack.remove();

        if (!argsStack.isEmpty())
        {
            if (argsStack.size() < 3)
                throw new TranslatedCommandException(getCommandUsage(sender));
            x = parseInt(argsStack.remove());
            y = parseInt(argsStack.remove());
            z = parseInt(argsStack.remove());
        }
        else
        {
            if (sender instanceof CommandBlockLogic)
            {
                CommandBlockLogic cb = (CommandBlockLogic) sender;
                world = cb.getEntityWorld();
                BlockPos coords = cb.getPosition();
                x = coords.getX();
                y = coords.getY();
                z = coords.getZ();
            }
            else
                throw new TranslatedCommandException(getCommandUsage(sender));
        }

        if (!argsStack.isEmpty())
        {
            world = DimensionManager.getWorld(parseInt(argsStack.remove()));
            if (world == null)
                throw new TranslatedCommandException("This dimension does not exist");
        }
        AxisAlignedBB pool = AxisAlignedBB.fromBounds(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1, z + radius + 1);
        CommandButcherTickTask.schedule(sender, world, mobType, pool, radius);
    }

}
