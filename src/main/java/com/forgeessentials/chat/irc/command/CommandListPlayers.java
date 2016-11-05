package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.server.MinecraftServer;

import com.forgeessentials.chat.irc.IrcCommand.IrcCommandParser;
import com.forgeessentials.util.FeCommandParserArgs;

public class CommandListPlayers extends IrcCommandParser
{

    @Override
    public Collection<String> getCommandNames()
    {
        return Arrays.asList("list", "online", "players");
    }

    @Override
    public String getCommandUsage()
    {
        return "";
    }

    @Override
    public String getCommandHelp()
    {
        return "Show list of online players";
    }

    @Override
    public boolean isAdminCommand()
    {
        return false;
    }

    @Override
    public void parse(FeCommandParserArgs arguments)
    {
        arguments.confirm("List of players:");
        for (String username : MinecraftServer.getServer().getConfigurationManager().getAllUsernames())
            arguments.confirm(" - " + username);
    }

}
