package com.forgeessentials.chat.command;

import java.util.List;

import org.jline.reader.impl.DefaultParser.ArgumentList;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.TranslatedCommandException.PlayerNotFoundException;
import com.forgeessentials.core.misc.TranslatedCommandException.WrongUsageException;
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
                .then(Commands.argument("player", EntityArgument.playerIrc())//Implement IrcHandler.getInstance().getIrcUserNames() somehow
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
        String name = EntityArgument.playerIrc();
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
    }
}
