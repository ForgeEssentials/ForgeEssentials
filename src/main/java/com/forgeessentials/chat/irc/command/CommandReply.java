package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcCommand;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException.PlayerNotFoundException;
import com.forgeessentials.core.misc.TranslatedCommandException.WrongUsageException;

public class CommandReply implements IrcCommand
{

    @Override
    public Collection<String> getCommandNames()
    {
        return Arrays.asList("reply", "r");
    }

    @Override
    public String getUsage()
    {
        return "<message...>";
    }

    @Override
    public String getCommandHelp()
    {
        return "Reply to the last private message";
    }

    @Override
    public boolean isAdminCommand()
    {
        return false;
    }

    @Override
    public void processCommand(CommandSource sender, String[] args) throws CommandException
    {
        if (args.length < 1)
            throw new WrongUsageException("commands.message.usage", new Object[0]);

        CommandSource target = com.forgeessentials.chat.command.CommandReply.getReplyTarget(sender);
        if (target == null)
            throw new PlayerNotFoundException("commands.generic.player.notFound");

        if (target == sender)
            throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);

        ModuleChat.tell(sender, ForgeEssentialsCommandBuilder.getChatComponentFromNthArg(args, 0), target);
    }
}
