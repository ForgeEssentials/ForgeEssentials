package com.forgeessentials.teleport.commands;

import java.util.HashMap;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTppos extends ForgeEssentialsCommandBuilder
{

    public CommandTppos(boolean enabled)
    {
        super(enabled);
    }

    /**
     * Spawn point for each dimension
     */
    public static HashMap<Integer, Point> spawnPoints = new HashMap<>();

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "tppos";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer sender = getServerPlayer(ctx.getSource());
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        TeleportHelper.teleport(sender, new WarpPoint(sender.level.dimension(), pos, sender.getXRot(), sender.getYRot()));
        return Command.SINGLE_SUCCESS;
    }

}
