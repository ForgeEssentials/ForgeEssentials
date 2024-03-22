package com.forgeessentials.teleport.commands;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.world.level.material.Material;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTop extends ForgeEssentialsCommandBuilder
{

    public CommandTop(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "top";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext, "player")))
                .executes(CommandContext -> execute(CommandContext, "me"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("me"))
        {

            top(getServerPlayer(ctx.getSource()));
        }
        else if (params.equals("player") && hasPermission(ctx.getSource(), TeleportModule.PERM_TOP_OTHERS))
        {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            if (!player.hasDisconnected())
            {
                top(player);
            }
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator
                        .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("me"))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You are not a player.");
            return Command.SINGLE_SUCCESS;
        }
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        if (!player.hasDisconnected())
        {
            top(player);
        }
        else
        {
            ChatOutputHandler.chatError(ctx.getSource(), Translator
                    .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    public void top(ServerPlayer player) throws CommandRuntimeException
    {
        WarpPoint point = new WarpPoint(player);
        int oldY = point.getBlockY();
        int precY = player.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, player.blockPosition()).getY();

        if (oldY != precY)
        {
            if (!ForgeEssentials.isCubicChunksInstalled && precY == -1)
            {
                point.setY(0);
                while (player.level.getBlockState(point.getBlockPos()).getMaterial() != Material.AIR)
                {
                    point.setY(point.getY() + 1);
                }
                if (oldY == point.getBlockY())
                {
                    return;
                }
            }
            else
            {
                point.setY(precY);
            }
            TeleportHelper.teleport(player, point);
        }
    }

}
