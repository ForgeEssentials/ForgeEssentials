package com.forgeessentials.chat.irc.command;

import java.util.Arrays;
import java.util.Collection;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcCommand;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponent;

public class CommandMessage implements IrcCommand
{

    @Override
    public Collection<String> getCommandNames()
    {
        return Arrays.asList("msg", "m");
    }

    @Override
    public String getUsage()
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
    public void processCommand(CommandSource sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            ChatOutputHandler.chatError(sender, "No player specified!");
            return;
        }

        UserIdent player = UserIdent.get(args[0], true);
        if (args.length < 2)
        {
            ChatOutputHandler.chatError(sender, "No message specified");
            return;
        }

        TextComponent msg = ForgeEssentialsCommandBuilder.getChatComponentFromNthArg(args, 2);
        ModuleChat.tell(sender, msg, player.getPlayer().createCommandSourceStack());
    }

}
