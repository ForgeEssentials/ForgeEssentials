package com.forgeessentials.chat.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandIrcBot extends ParserCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "ircbot";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/ircbot [reconnect|disconnect] Connect or disconnect the IRC server bot.";
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
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        switch(arguments.remove())
        {
        case "connect":
        case "reconnect":
            IrcHandler.getInstance().connect();
            break;
        case "disconnect":
            IrcHandler.getInstance().disconnect();
            break;
        default:
            arguments.notify("IRC bot is " + (IrcHandler.getInstance().isConnected() ? "online" : "offline"));
            break;
        }
    }

}
