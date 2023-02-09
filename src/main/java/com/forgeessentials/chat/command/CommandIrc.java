package com.forgeessentials.chat.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.TranslatedCommandException.WrongUsageException;
import com.mojang.brigadier.Command;
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
        return builder
                .then(Commands.argument("message", MessageArgument.message())
                        .executes(CommandContext -> execute(CommandContext)
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (!IrcHandler.getInstance().isConnected())
            throw new TranslatedCommandException("Not connected to IRC!");
        ITextComponent message = MessageArgument.getMessage(ctx, "message");
        if (message.getString() == null || message.getString() == "")
        {
            throw new WrongUsageException("commands.message.usage");
        }
        else
        {
            IrcHandler.getInstance().sendPlayerMessage(ctx.getSource(), message);
        }
        return Command.SINGLE_SUCCESS;
    }
}
