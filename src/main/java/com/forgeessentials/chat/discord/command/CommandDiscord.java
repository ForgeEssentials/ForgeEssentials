package com.forgeessentials.chat.discord.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.discord.DiscordHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandDiscord extends ForgeEssentialsCommandBuilder
{

    private DiscordHandler handler;

    public CommandDiscord(DiscordHandler handler)
    {
        super(true);
        this.handler = handler;
    }
    @Override public String getPrimaryAlias()
    {
        return "discord";
    }

    @Override public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("select", StringArgumentType.word())
                        .then(Commands.argument("channel", StringArgumentType.word())
                        .executes(CommandContext -> execute(CommandContext, "channel")
                        ))
                );
    }

    @Override public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override public String getPermissionNode()
    {
        return ModuleChat.PERM + ".discord";
    }

    @Override public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override public int processCommandConsole(CommandContext<CommandSource> ctx, String params)  throws CommandSyntaxException
    {
        if ("channel".equals(params))
        {
                String channel = StringArgumentType.getString(ctx, "channel");
                if (handler.channels.contains(channel)) {
                    handler.selectedChannel = channel;
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Channel #%s selected!"));
                    return Command.SINGLE_SUCCESS;
                } else {
                    ChatOutputHandler.chatError(ctx.getSource(), Translator.format("Unknown Channel: %s", channel));
                    return Command.SINGLE_SUCCESS;
                }
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override public int processCommandPlayer(CommandContext<CommandSource> ctx, String params)
    {
        return Command.SINGLE_SUCCESS;
    }
}
