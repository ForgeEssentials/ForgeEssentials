package com.forgeessentials.chat.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.commands.Arguments.FeIrcPlayerArgument;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.TranslatedCommandException.PlayerNotFoundException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandIrcPm extends BaseCommand
{

    public CommandIrcPm(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }


    @Override
    public String getPrimaryAlias()
    {
        return "ircpm";
    }


    @Override
    public String getPermissionNode()
    {
        return "fe.chat.ircpm";
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
                .then(Commands.argument("player", FeIrcPlayerArgument.player())
                        .then(Commands.argument("message", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "help")
                                        )
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (!IrcHandler.getInstance().isConnected())
            throw new TranslatedCommandException("Not connected to IRC!");
        String name = FeIrcPlayerArgument.getPlayer(ctx, "player");
        CommandSource target = IrcHandler.getInstance().getIrcUser(name);
        if (target == null)
        {
            throw new PlayerNotFoundException("commands.generic.player.notFound");
        }
        else if (target == ctx.getSource())
        {
            throw new PlayerNotFoundException("commands.message.sameTarget");
        }
        else
        {
            
            ITextComponent message = MessageArgument.getMessage(ctx, "message");
            ModuleChat.tell(ctx.getSource(), message, target);
        }
        return Command.SINGLE_SUCCESS;
    }
}
