package com.ForgeEssentials.core.commands;

import com.ForgeEssentials.util.Localization;

import net.minecraft.command.CommandException;

public class PermissionDeniedException extends CommandException
{
	
	public PermissionDeniedException()
	{
		super(Localization.ERROR_NOPERMISSION, new Object[] {});
	}

	public PermissionDeniedException(String par1Str, Object... par2ArrayOfObj)
	{
		super(par1Str, par2ArrayOfObj);
	}

}
