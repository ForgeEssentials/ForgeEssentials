package com.forgeessentials.core.commands;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import com.forgeessentials.util.CommandParserArgs;

public abstract class ParserCommandBase extends ForgeEssentialsCommandBase
{

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender);
        parse(arguments);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender, true);
        try
        {
            parse(arguments);
        }
        catch (CommandException e)
        {
            return null;
        }
        return arguments.tabCompletion;
    }

    public abstract void parse(CommandParserArgs arguments);

}
