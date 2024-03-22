package com.forgeessentials.commands.player;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class CommandSeen extends ForgeEssentialsCommandBuilder
{

    public CommandSeen(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
        		.then(Commands.argument("player", StringArgumentType.word())
        				.suggests(SUGGEST_PLAYERS)
        				.executes(CommandContext -> execute(CommandContext, "player")));
    }

    public final SuggestionProvider<CommandSourceStack> SUGGEST_PLAYERS = (ctx, builder) -> {
    	List<String> names = new ArrayList<>();;
    	for(String s : getAllPlayernames()) {
    		if(!(s.contains("$")||s.contains("_"))) {
    			names.add(s);
    		}
    	}
        return SharedSuggestionProvider.suggest(names, builder);
    };

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        UserIdent player = UserIdent.get(StringArgumentType.getString(ctx, "player"), false);

        if (player.hasPlayer() && !player.getPlayerMP().hasDisconnected())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Player %s is currently online", player.getUsernameOrUuid()));
            return Command.SINGLE_SUCCESS;
        }

        if (!player.hasUuid() || !PlayerInfo.exists(player.getUuid()))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Player not found");
            return Command.SINGLE_SUCCESS;
        }
        PlayerInfo pi = PlayerInfo.get(player.getUuid());
        long t = (System.currentTimeMillis() - pi.getLastLogout().getTime()) / 1000;
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Player %s was last seen %s ago",
                player.getUsernameOrUuid(), ChatOutputHandler.formatTimeDurationReadable(t, false)));
        PlayerInfo.discard(pi.ident.getUuid());
        return Command.SINGLE_SUCCESS;
    }
}
