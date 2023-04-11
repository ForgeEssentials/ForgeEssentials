package com.forgeessentials.chat.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandIrc extends ForgeEssentialsCommandBuilder
{

    public CommandIrc(boolean enabled)
    {
        super(enabled);
    }


    @Override
    public String getPrimaryAlias()
    {
        return "irc";
    }


    @Override
    public String getPermissionNode()
    {
        return "fe.chat.irc";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(CommandContext -> execute(CommandContext, "blank")
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (!IrcHandler.getInstance().isConnected()) {
            ChatOutputHandler.chatError(ctx.getSource(), "Not connected to IRC!");
            return Command.SINGLE_SUCCESS;
        }
        IrcHandler.getInstance().sendPlayerMessage(ctx.getSource(), new StringTextComponent(StringArgumentType.getString(ctx, "message")));
        return Command.SINGLE_SUCCESS;
    }
}
