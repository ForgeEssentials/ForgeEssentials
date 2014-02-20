package com.forgeessentials.core.commands;

import net.minecraft.command.CommandException;

import com.forgeessentials.util.Localization;

public class PermissionDeniedException extends CommandException
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7011906314181110110L;

	public PermissionDeniedException()
	{
		super(Localization.ERROR_NOPERMISSION, new Object[] {});
	}

	public PermissionDeniedException(String par1Str, Object... par2ArrayOfObj)
	{
		super(par1Str, par2ArrayOfObj);
	}

}
