package com.forgeessentials.commands.world;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.CommandButcherTickTask;
import com.forgeessentials.commands.util.CommandButcherTickTask.ButcherMobType;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandButcher extends BaseCommand
{

    public static List<String> typeList = ButcherMobType.getNames();

    @Override
    public String getPrimaryAlias()
    {
        return "butcher";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "butcher" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".butcher";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender par1ICommandSender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "-1");
        }
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, typeList);
        }
        return null;
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity sender, String[] args) throws CommandException
    {
        int radius = -1;
        double x = sender.position().x;
        double y = sender.position().y;
        double z = sender.position().z;
        World world = sender.level;
        String mobType = ButcherMobType.HOSTILE.toString();

        Queue<String> argsStack = new LinkedList<>(Arrays.asList(args));
        if (!argsStack.isEmpty())
        {
            String radiusValue = argsStack.remove();
            if (radiusValue.equalsIgnoreCase("world"))
                radius = -1;
            else
                radius = parseInt(radiusValue, -1, Integer.MAX_VALUE);
        }

        if (!argsStack.isEmpty())
            mobType = argsStack.remove();

        if (!argsStack.isEmpty())
        {
            if (argsStack.size() < 3)
                throw new TranslatedCommandException("Improper syntax: <radius> [type] [x y z] [world]", Integer.MAX_VALUE);
            x = parseDouble(argsStack.remove(), sender.position().x);
            y = parseDouble(argsStack.remove(), sender.position().y);
            z = parseDouble(argsStack.remove(), sender.position().z);
        }

        if (!argsStack.isEmpty())
        {
            world = DimensionManager.getWorld(parseInt(argsStack.remove()));
            if (world == null)
                throw new TranslatedCommandException("The specified dimension does not exist");
        }

        AxisAlignedBB pool = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1, z + radius + 1);
        CommandButcherTickTask.schedule(sender, world, mobType, pool, radius);
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
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
                radius = parseInt(radiusValue, 0, Integer.MAX_VALUE);
        }

        if (!argsStack.isEmpty())
            mobType = argsStack.remove();

        if (!argsStack.isEmpty())
        {
            if (argsStack.size() < 3)
                throw new TranslatedCommandException(getUsage(sender), Integer.MAX_VALUE);
            x = parseInt(argsStack.remove());
            y = parseInt(argsStack.remove());
            z = parseInt(argsStack.remove());
        }
        else
        {
            if (sender instanceof CommandBlockLogic)
            {
                CommandBlockLogic cb = (CommandBlockLogic) sender;
                world = cb.getLevel();
                Vector3d coords = cb.getPosition();
                x = coords.x;
                y = coords.y;
                z = coords.z;
            }
        }

        if (!argsStack.isEmpty())
        {
            world = DimensionManager.getWorld(parseInt(argsStack.remove()));
            if (world == null)
                throw new TranslatedCommandException("This dimension does not exist");
        }
        AxisAlignedBB pool = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1, z + radius + 1);
        CommandButcherTickTask.schedule(sender, world, mobType, pool, radius);
    }

}
