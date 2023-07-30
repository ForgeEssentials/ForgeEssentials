package com.forgeessentials.teleport.commands;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandBed extends ForgeEssentialsCommandBuilder
{

    public CommandBed(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "bed";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (hasPermission(ctx.getSource(), TeleportModule.PERM_BED_OTHERS))
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (!player.hasDisconnected())
            {
                tp(player);
            }
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator
                        .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
        }
        else
        {
            tp(getServerPlayer(ctx.getSource()));
        }
        return Command.SINGLE_SUCCESS;
    }

    private void tp(ServerPlayerEntity player) throws CommandException
    {
        World world = ServerLifecycleHooks.getCurrentServer().getLevel(player.getRespawnDimension());
        if (world == null)
        {
            ChatOutputHandler.chatError(player, "No respawn dim found.");
            return;
        }

        BlockPos spawn = player.getRespawnPosition();
        if (spawn == null)
        {
            ChatOutputHandler.chatError(player, "No respawn position found.");
            return;
        }

        PlayerInfo.get(player.getGameProfile().getId()).setLastTeleportOrigin(new WarpPoint(player));
        WarpPoint spawnPoint = new WarpPoint(world.dimension(), spawn, player.xRot, player.yRot);
        TeleportHelper.teleport(player, spawnPoint);
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        if (!player.hasDisconnected())
        {
            tp(player);
        }
        else
        {
            ChatOutputHandler.chatError(ctx.getSource(), Translator
                    .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

}
