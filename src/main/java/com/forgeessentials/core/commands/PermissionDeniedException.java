package com.forgeessentials.core.commands;

import com.forgeessentials.core.misc.Translator;

import net.minecraft.command.CommandException;

public class PermissionDeniedException extends CommandException
{

    /**
     *
     */
    private static final long serialVersionUID = 7011906314181110110L;

    public PermissionDeniedException()
    {
        super(Translator.translateITC("You don't have permissions for that."));
    }

}
