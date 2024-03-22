package com.forgeessentials.teleport.commands;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.RespawnHandler;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandSpawn extends ForgeEssentialsCommandBuilder
{

    public CommandSpawn(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "spawn";
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
        if (params.equals("player"))
        {
            if (!hasPermission(ctx.getSource(), TeleportModule.PERM_SPAWN_OTHERS))
            {
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
                return Command.SINGLE_SUCCESS;
            }
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            if (player.hasDisconnected())
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator
                        .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }

            WarpPoint point = RespawnHandler.getSpawn(player, null);
            if (point == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "There is no spawnpoint set for that player.");
                return Command.SINGLE_SUCCESS;
            }

            TeleportHelper.teleport(player, point);
        }
        if (params.equals("me"))
        {
            ServerPlayer player = getServerPlayer(ctx.getSource());

            WarpPoint point = RespawnHandler.getSpawn(player, null);
            if (point == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "You have no spawnpoint");
                return Command.SINGLE_SUCCESS;
            }

            PlayerInfo.get(player.getGameProfile().getId()).setLastTeleportOrigin(new WarpPoint(player));
            ChatOutputHandler.chatConfirmation(player, "Teleporting to spawn.");
            TeleportHelper.teleport(player, point);
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("me"))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You need to specify a player");
            return Command.SINGLE_SUCCESS;
        }
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        if (player.hasDisconnected())
        {
            ChatOutputHandler.chatError(ctx.getSource(), Translator
                    .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
            return Command.SINGLE_SUCCESS;
        }

        WarpPoint point = RespawnHandler.getSpawn(player, null);
        if (point == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "There is no spawnpoint set for that player.");
            return Command.SINGLE_SUCCESS;
        }

        TeleportHelper.teleport(player, point);
        return Command.SINGLE_SUCCESS;
    }

}
