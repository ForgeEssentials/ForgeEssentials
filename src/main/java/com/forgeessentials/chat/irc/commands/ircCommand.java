package com.forgeessentials.chat.irc.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.pircbotx.User;

public abstract class ircCommand {
    // Get functions
    public final String getCommandName()
    {
        return this.getClass().getSimpleName().replace("ircCommand", "");
    }

    public final String getAliasesAsString()
    {
        return Arrays.toString(getAliases());
    }

    // Checks
    public final boolean isCommand(String commandName)
    {
        return (commandName.equalsIgnoreCase(this.getCommandName()) || this.isAlias(commandName));
    }

    public final boolean isAlias(String commandName)
    {
        if (getAliases().length == 0)
        {
            return false;
        }
        for (String alias : getAliases())
        {
            if (commandName.equalsIgnoreCase(alias))
            {
                return true;
            }
        }
        return false;
    }

    // Misc.
    @Override
    public String toString()
    {
        return "Command: " + getCommandName() + " \nAliases: " + Arrays.toString(getAliases()) + " \nUsage: " + getCommandUsage() + " \nInfo: "
                + getCommandInfo();
    }

    // Overloading
    public final void execute(ArrayList<String> args, User user)
    {
        execute(args.toArray(new String[args.size()]), user);
    }

    // To be implemented by commands
    public abstract String[] getAliases();        // To be done in locale eventually.

    public abstract String getCommandInfo();    // To be done in locale eventually.

    public abstract String getCommandUsage();    // To be done in locale eventually.

    public abstract void execute(String[] args, User user);
}
