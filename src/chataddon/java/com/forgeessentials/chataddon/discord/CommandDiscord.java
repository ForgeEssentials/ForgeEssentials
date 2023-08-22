package com.forgeessentials.chataddon.discord;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandDiscord extends ForgeEssentialsCommandBuilder
{

    public CommandDiscord(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "discord";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("select").then(Commands.argument("channel", StringArgumentType.string())
                        .suggests(SUGGEST_CHANNELS).executes(CommandContext -> execute(CommandContext, "channel"))));
    }

    public static final SuggestionProvider<CommandSource> SUGGEST_CHANNELS = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>(ModuleDiscordBridge.instance.channels);
        return ISuggestionProvider.suggest(listArgs, builder);
    };

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
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if ("channel".equals(params))
        {
            String channel = StringArgumentType.getString(ctx, "channel");
            if (ModuleDiscordBridge.instance.channels.contains(channel))
            {
            	ModuleDiscordBridge.instance.selectedChannel = channel;
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Channel #%s selected!"));
            }
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator.format("Unknown Channel: %s", channel));
            }
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }
}
