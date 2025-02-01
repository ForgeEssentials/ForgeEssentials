package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.util.IChatComponent;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcCommand.IrcCommandParser;
import com.forgeessentials.util.CommandParserArgs;

public class CommandMessage extends IrcCommandParser
{

    @Override
    public Collection<String> getCommandNames()
    {
        return Arrays.asList("msg", "m");
    }

    @Override
    public String getCommandUsage()
    {
        return "<player> <message...>";
    }

    @Override
    public String getCommandHelp()
    {
        return "Send a private message to a player";
    }

    @Override
    public boolean isAdminCommand()
    {
        return false;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.error("No player specified!");
            return;
        }

        UserIdent player = arguments.parsePlayer(true, true);
        
        if (arguments.isEmpty())
        {
            arguments.error("No message specified");
            return;
        }

        IChatComponent msg = CommandBase.getChatComponentFromNthArg(arguments.sender, arguments.toArray(), 0, true);
        ModuleChat.tell(arguments.sender, msg, player.getPlayer());
    }

}
