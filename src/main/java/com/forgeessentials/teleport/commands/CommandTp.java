package com.forgeessentials.teleport.commands;

import java.util.HashMap;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.Point;
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

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTp extends ForgeEssentialsCommandBuilder
{

    public CommandTp(boolean enabled)
    {
        super(enabled);
    }

    /**
     * Spawn point for each dimension
     */
    public static HashMap<Integer, Point> spawnPoints = new HashMap<>();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .then(Commands.literal("toPlayer")
                        .then(Commands.argument("toplayer", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "others"))))
                .then(Commands.literal("toPosition")
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(CommandContext -> execute(CommandContext, "pos"))))
                .executes(CommandContext -> execute(CommandContext, "to")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer sender = getServerPlayer(ctx.getSource());
        if (params.equals("to"))
        {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");

            if (target.hasDisconnected())
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator
                        .format("Player %s does not exist, or is not online.", target.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
            TeleportHelper.teleport(sender, new WarpPoint(target));
        }
        else if (params.equals("others")
                && APIRegistry.perms.checkPermission(sender, TeleportModule.PERM_TP_OTHERS))
        {

            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            if (!player.hasDisconnected())
            {
                ServerPlayer target = EntityArgument.getPlayer(ctx, "toplayer");

                if (!target.hasDisconnected())
                {
                    PlayerInfo playerInfo = PlayerInfo.get(player.getGameProfile().getId());
                    playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                    WarpPoint point = new WarpPoint(target);
                    TeleportHelper.teleport(player, point);
                }
                else
                {
                    ChatOutputHandler.chatError(ctx.getSource(), Translator.format(
                            "Player %s does not exist, or is not online.", target.getDisplayName().getString()));
                    return Command.SINGLE_SUCCESS;
                }
            }
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator
                        .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
        }
        else if (params.equals("pos"))
        {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "position");
            PlayerInfo playerInfo = PlayerInfo.get(player.getGameProfile().getId());
            playerInfo.setLastTeleportOrigin(new WarpPoint(player));
            TeleportHelper.teleport(player, new WarpPoint(player.level.dimension(), pos, player.getXRot(), player.getYRot()));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        CommandSourceStack source = ctx.getSource();
        if (params.equals("others"))
        {

            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            if (!player.hasDisconnected())
            {
                ServerPlayer target = EntityArgument.getPlayer(ctx, "toplayer");

                if (!target.hasDisconnected())
                {
                    PlayerInfo playerInfo = PlayerInfo.get(player.getGameProfile().getId());
                    playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                    WarpPoint point = new WarpPoint(target);
                    TeleportHelper.teleport(player, point);
                }
                else
                {
                    ChatOutputHandler.chatError(ctx.getSource(), Translator.format(
                            "Player %s does not exist, or is not online.", target.getDisplayName().getString()));
                    return Command.SINGLE_SUCCESS;
                }
            }
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator
                        .format("Player %s does not exist, or is not online.", player.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }
        }
        else if (params.equals("pos"))
        {
            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
            BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "position");
            PlayerInfo playerInfo = PlayerInfo.get(player.getGameProfile().getId());
            playerInfo.setLastTeleportOrigin(new WarpPoint(player));
            TeleportHelper.teleport(player, new WarpPoint(player.level.dimension(), pos, player.getXRot(), player.getYRot()));
        }
        else
        {
            ChatOutputHandler.chatError(source, Translator.translate("Console must use a subcommand!"));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "tp";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

}
