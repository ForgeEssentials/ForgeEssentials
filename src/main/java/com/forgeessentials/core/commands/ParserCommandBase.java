package com.forgeessentials.core.commands;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.CommandParserArgs.CancelParsingException;

public abstract class ParserCommandBase extends ForgeEssentialsCommandBase
{

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        CommandParserArgs arguments = new CommandParserArgs(this, args, sender);
        try
        {
            parse(arguments);
        }
        catch (CancelParsingException e)
        {
            /* do nothing */
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
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

    public abstract void parse(CommandParserArgs arguments) throws CommandException;

}
