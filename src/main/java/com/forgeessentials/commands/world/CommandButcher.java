package com.forgeessentials.commands.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forgeessentials.commands.util.CommandButcherTickTask;
import com.forgeessentials.commands.util.CommandButcherTickTask.ButcherMobType;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandButcher extends ForgeEssentialsCommandBuilder
{

    public CommandButcher(boolean enabled)
    {
        super(enabled);
    }

    public static List<String> typeList = ButcherMobType.getNames();

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "butcher";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.then(Commands.argument("radius", IntegerArgumentType.integer(-1)).then(Commands
                .argument("mob", StringArgumentType.string()).suggests(mob_types)
                .then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands
                        .argument("world", DimensionArgument.dimension())
                        .executes(CommandContext -> execute(CommandContext, Integer
                                .toString(IntegerArgumentType.getInteger(CommandContext, "radius")) + "&&"
                                + StringArgumentType.getString(CommandContext, "mob") + "&&"
                                + Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX())
                                + "&&"
                                + Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX())
                                + "&&"
                                + Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX())
                                + "&&" + DimensionArgument.getDimension(CommandContext, "world").dimension().location()
                                        .toString()))))))
                .executes(CommandContext -> execute(CommandContext, "help"));
    }

    public static final SuggestionProvider<CommandSource> mob_types = (ctx, builder) -> ISuggestionProvider.suggest(typeList, builder);

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.toString().equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Use /butcher <radius> [type] [x y z] [world]");
            return Command.SINGLE_SUCCESS;
        }
        List<String> args = new ArrayList<>(Arrays.asList(params.split("&&")));

        ServerPlayerEntity sender = getServerPlayer(ctx.getSource());
        int radius = -1;
        double x = sender.position().x;
        double y = sender.position().y;
        double z = sender.position().z;
        ServerWorld world = sender.getLevel();
        String mobType = ButcherMobType.HOSTILE.toString();

        if (!args.isEmpty())
        {
            String radiusValue = args.remove(0);
            if (radiusValue.equalsIgnoreCase("world"))
                radius = -1;
            else
                radius = parseInt(radiusValue, -1, Integer.MAX_VALUE);
        }

        if (!args.isEmpty())
            mobType = args.remove(0);

        if (!args.isEmpty())
        {
            if (args.size() < 3)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Improper syntax: <radius> [type] [x y z] [world]");
                return Command.SINGLE_SUCCESS;
            }
            x = parseDouble(args.remove(0));
            y = parseDouble(args.remove(0));
            z = parseDouble(args.remove(0));
        }

        if (!args.isEmpty())
        {
            world = ServerUtil.getWorldFromString(args.remove(0));
            if (world == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "The specified dimension does not exist");
                return Command.SINGLE_SUCCESS;
            }
        }

        AxisAlignedBB pool = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1,
                z + radius + 1);
        CommandButcherTickTask.schedule(sender.createCommandSourceStack(), world, mobType, pool, radius);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        int radius = -1;
        double x = 0;
        double y = 0;
        double z = 0;
        ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
        String mobType = ButcherMobType.HOSTILE.toString();
        List<String> args = new ArrayList<>(Arrays.asList(params.split("&&")));

        if (!args.isEmpty())
        {
            String radiusValue = args.remove(0);
            if (radiusValue.equalsIgnoreCase("world"))
                radius = -1;
            else
                radius = parseInt(radiusValue, 0, Integer.MAX_VALUE);
        }

        if (!args.isEmpty())
            mobType = args.remove(0);

        if (!args.isEmpty())
        {
            if (args.size() < 3)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Improper syntax: <radius> [type] [x y z] [world]");
                return Command.SINGLE_SUCCESS;
            }
            x = parseInt(args.remove(0));
            y = parseInt(args.remove(0));
            z = parseInt(args.remove(0));
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

        if (!args.isEmpty())
        {
            world = ServerUtil.getWorldFromString(args.remove(0));
            if (world == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "This dimension does not exist");
                return Command.SINGLE_SUCCESS;
            }
        }
        AxisAlignedBB pool = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1,
                z + radius + 1);
        CommandButcherTickTask.schedule(ctx.getSource(), world, mobType, pool, radius);
        return Command.SINGLE_SUCCESS;
    }

}
