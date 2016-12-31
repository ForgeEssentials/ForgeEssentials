package com.forgeessentials.util;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import com.forgeessentials.commons.CommandParserArgs;
import com.forgeessentials.util.ForgeEssentialsCommandBase;

public abstract class ParserCommandBase extends ForgeEssentialsCommandBase
{

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        CommandParserArgs arguments = CommandParserArgs.createInstance(this, args, sender);
        try
        {
            parse(arguments);
        }
        catch (CommandParserArgs.CancelParsingException e)
        {
            /* do nothing */
        }
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
            return arguments.tabCompletion;
        }
        return arguments.tabCompletion;
    }

    public abstract void parse(CommandParserArgs arguments);

}
