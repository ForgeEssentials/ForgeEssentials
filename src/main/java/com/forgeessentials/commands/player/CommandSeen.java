package com.forgeessentials.commands.player;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandSeen extends ForgeEssentialsCommandBuilder
{

    public CommandSeen(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "seen";
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".seen";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("player", StringArgumentType.greedyString())
                        .executes(CommandContext -> execute(CommandContext, "player")
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        UserIdent player = UserIdent.get(StringArgumentType.getString(ctx, "player"),false);

        if (player.hasPlayer()&&!player.getPlayerMP().hasDisconnected())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Player %s is currently online", player.getUsernameOrUuid()));
            return Command.SINGLE_SUCCESS;
        }

        if (!player.hasUuid() || !PlayerInfo.exists(player.getUuid())) {
            ChatOutputHandler.chatError(ctx.getSource(), Translator.format("Player %s is currently online", player.getUsername()));
            return Command.SINGLE_SUCCESS;
        }
        PlayerInfo pi = PlayerInfo.get(player.getUuid());
        long t = (System.currentTimeMillis() - pi.getLastLogout().getTime()) / 1000;
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Player %s was last seen %s ago", player.getUsernameOrUuid(),
                ChatOutputHandler.formatTimeDurationReadable(t, false)));
        PlayerInfo.discard(pi.ident.getUuid());
        return Command.SINGLE_SUCCESS;
    }
}
