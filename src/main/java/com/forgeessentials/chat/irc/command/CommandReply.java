package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcCommand;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.output.ChatOutputHandler;

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
        if (args.length < 1) {
            ChatOutputHandler.chatError(sender, Translator.format("Invalid Syntax"));
            return;
        }

        CommandSource target = com.forgeessentials.chat.command.CommandReply.getReplyTarget(CommandUtils.getServerPlayer(sender)).createCommandSourceStack();
        if (target == null) {
            ChatOutputHandler.chatError(sender, Translator.format("Player not found"));
            //return Command.SINGLE_SUCCESS;
            return;
        }

        if (target == sender) {
            ChatOutputHandler.chatError(sender, "You can't send a message to your self");
            //return Command.SINGLE_SUCCESS;
            return;
        }

        ModuleChat.tell(sender, ForgeEssentialsCommandBuilder.getChatComponentFromNthArg(args, 0), target);
    }
}
