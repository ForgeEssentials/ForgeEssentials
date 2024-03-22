package com.forgeessentials.teleport.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.permissions.commands.PermissionCommandParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandSetSpawn extends ForgeEssentialsCommandBuilder
{

    public CommandSetSpawn(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM_SETSPAWN = "fe.perm.setspawn";

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "setspawn";
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("bed")
                        .then(Commands.literal("enable")
                                .executes(CommandContext -> execute(CommandContext, "bed-enable")))
                        .then(Commands.literal("disable")
                                .executes(CommandContext -> execute(CommandContext, "bed-disable"))))
                .then(Commands.literal("here").executes(CommandContext -> execute(CommandContext, "here")))
                .then(Commands.literal("clear").executes(CommandContext -> execute(CommandContext, "clear")))
                .then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands
                        .argument("dim", DimensionArgument.dimension())
                        .executes(CommandContext -> execute(CommandContext, Integer
                                .toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX()) + "&&"
                                + Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getY())
                                + "&&"
                                + Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getZ())
                                + "&&"
                                + DimensionArgument.getDimension(CommandContext, "dim").dimension().location()
                                        .toString()))))
                .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")))
                .executes(CommandContext -> execute(CommandContext, "help"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        List<String> args = new ArrayList<>(Arrays.asList(params.split("&&")));
        PermissionCommandParser.parseGroupSpawn(ctx, args, Zone.GROUP_DEFAULT, APIRegistry.perms.getServerZone(), true);
        return Command.SINGLE_SUCCESS;
    }

}
