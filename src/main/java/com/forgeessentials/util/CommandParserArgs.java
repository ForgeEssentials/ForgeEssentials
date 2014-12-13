package com.forgeessentials.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 *
 */
public class CommandParserArgs {

    public final String command;
    public final Queue<String> args;
    public final ICommandSender sender;
    public final EntityPlayerMP senderPlayer;
    public final boolean isTabCompletion;

    public List<String> tabCompletion = null;

    public CommandParserArgs(String command, String[] args, ICommandSender sender, boolean isTabCompletion)
    {
        this.command = command;
        this.args = new LinkedList<String>(Arrays.asList(args));
        this.sender = sender;
        this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
        this.isTabCompletion = isTabCompletion;
    }

    public CommandParserArgs(String commandName, String[] args, ICommandSender sender)
    {
        this(commandName, args, sender, false);
    }

    public void info(String message)
    {
        if (!isTabCompletion)
            OutputHandler.chatConfirmation(sender, message);
    }

    public void warn(String message)
    {
        if (!isTabCompletion)
            OutputHandler.chatWarning(sender, message);
    }

    public void error(String message)
    {
        if (!isTabCompletion)
            OutputHandler.chatError(sender, message);
    }
}
