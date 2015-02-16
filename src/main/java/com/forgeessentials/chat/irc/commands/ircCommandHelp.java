package com.forgeessentials.chat.irc.commands;

import org.pircbotx.User;

import com.forgeessentials.chat.irc.IRCHelper;

public class ircCommandHelp extends ircCommand {

    @Override
    public String[] getAliases()
    {
        return new String[] { };
    }

    @Override
    public String getCommandInfo()
    {
        return "Returns a list of commands and their usage";
    }

    @Override
    public String getCommandUsage()
    {
        return "%help";
    }

    @Override
    public void execute(String[] args, User user)
    {
    	IRCHelper.privateMessage(user,"Help:");
        for (ircCommand cmd : ircCommands.ircCommands)
        {
        	if ( !IRCHelper.twitchMode )
        	{
        		IRCHelper.privateMessage(user,"");
        		IRCHelper.privateMessage(user,"Command: " + cmd.getCommandName());
        		IRCHelper.privateMessage(user,"Aliases: " + cmd.getAliasesAsString());
        		IRCHelper.privateMessage(user,"Usage: " + cmd.getCommandUsage());
        		IRCHelper.privateMessage(user,"Info: " + cmd.getCommandInfo());
        	}
        	else
        	{        		
        		IRCHelper.privateMessage(user,": " + cmd.getCommandUsage() + " " + cmd.getCommandInfo() );
        	}
        }
    }

}
