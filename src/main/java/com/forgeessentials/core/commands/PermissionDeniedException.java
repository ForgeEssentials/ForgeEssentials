package com.forgeessentials.core.commands;

import net.minecraft.command.CommandException;

public class PermissionDeniedException extends CommandException
{

    /**
     *
     */
    private static final long serialVersionUID = 7011906314181110110L;

    public PermissionDeniedException()
    {
        super("You don't have permissions for that.", new Object[] {});
    }

    public PermissionDeniedException(String par1Str, Object... par2ArrayOfObj)
    {
        super(par1Str, par2ArrayOfObj);
    }

}
