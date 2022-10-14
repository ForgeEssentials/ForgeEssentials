package com.forgeessentials.chat.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandIrcBot extends BaseCommand
{

    public CommandIrcBot(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "ircbot";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.ircbot";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("connect")
                        .executes(CommandContext -> execute(CommandContext, "connect")
                                )
                        )
                .then(Commands.literal("reconnect")
                        .executes(CommandContext -> execute(CommandContext, "reconnect")
                                )
                        )
                .then(Commands.literal("disconnect")
                        .executes(CommandContext -> execute(CommandContext, "disconnect")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "info")
                        );
    }

    @SuppressWarnings("unlikely-arg-type")
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.equals("connect"))
        {
            //IDK this was empty in the 1.12.2 code
        }
        if (params.equals("reconnect"))
        {
            IrcHandler.getInstance().connect();
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("disconnect"))
        {
            IrcHandler.getInstance().disconnect();
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("info"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), Translator.format("IRC bot is ", (IrcHandler.getInstance().isConnected() ? "online" : "offline")));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }
}
