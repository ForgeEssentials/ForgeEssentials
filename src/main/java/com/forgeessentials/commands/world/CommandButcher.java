package com.forgeessentials.commands.world;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.CommandButcherTickTask;
import com.forgeessentials.commands.util.CommandButcherTickTask.ButcherMobType;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandButcher extends ForgeEssentialsCommandBuilder
{

    public CommandButcher(boolean enabled)
    {
        super(enabled);
    }

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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if(params.toString().equals("blank")) {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Use /butcher <radius> [type] [x y z] [world]");
            return Command.SINGLE_SUCCESS;
        }
        ServerPlayerEntity sender = getServerPlayer(ctx.getSource());
        int radius = -1;
        double x = sender.position().x;
        double y = sender.position().y;
        double z = sender.position().z;
        ServerWorld world = sender.getLevel();
        String mobType = ButcherMobType.HOSTILE.toString();

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
        CommandButcherTickTask.schedule(sender.createCommandSourceStack(), world, mobType, pool, radius);
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        int radius = -1;
        double x = 0;
        double y = 0;
        double z = 0;
        ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
        String mobType = ButcherMobType.HOSTILE.toString();

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
            if (GetSource(ctx.getSource()) instanceof CommandBlockLogic)
            {
                CommandBlockLogic cb = (CommandBlockLogic) GetSource(ctx.getSource());
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
        CommandButcherTickTask.schedule(ctx.getSource(), world, mobType, pool, radius);
    }

}
