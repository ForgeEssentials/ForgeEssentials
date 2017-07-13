package com.forgeessentials.chat.irc;

import java.util.Collection;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public interface IrcCommand
{

    public static final String COMMAND_CHAR = IrcHandler.COMMAND_CHAR;

    public Collection<String> getCommandNames();

    public String getUsage();

    public String getCommandHelp();

    public boolean isAdminCommand();

    public void processCommand(ICommandSender sender, String[] args) throws CommandException;

    public static abstract class IrcCommandParser implements IrcCommand
    {

        public static class IrcCommandParserArgs extends CommandParserArgs
        {

            public final IrcCommand ircCommand;

            public IrcCommandParserArgs(IrcCommand command, String[] args, ICommandSender sender)
            {
                super(null, args, sender, FMLCommonHandler.instance().getMinecraftServerInstance());
                ircCommand = command;
            }

            @Override
            public void error(String message, Object... args)
            {
                if (!isTabCompletion)
                    ChatOutputHandler.chatError(sender, "Error: " + Translator.format(message, args));
            }

        }

        @Override
        public abstract Collection<String> getCommandNames();

        @Override
        public abstract String getUsage();

        @Override
        public abstract String getCommandHelp();

        @Override
        public abstract boolean isAdminCommand();

        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException
        {
            CommandParserArgs arguments = new IrcCommandParserArgs(null, args, sender);
            parse(arguments);
        }

        public abstract void parse(CommandParserArgs arguments) throws CommandException;

    }

}
