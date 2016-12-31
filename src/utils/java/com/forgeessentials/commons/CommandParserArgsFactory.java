package com.forgeessentials.commons;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

/**
 * Created by alexa on 12/30/2016.
 */
public interface CommandParserArgsFactory
{
    public CommandParserArgs createInstance(ICommand command, String[] args, ICommandSender sender, boolean isTabCompletion);
}
