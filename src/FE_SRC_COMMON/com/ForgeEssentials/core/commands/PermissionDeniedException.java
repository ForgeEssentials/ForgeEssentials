package com.ForgeEssentials.core.commands;

import net.minecraft.command.CommandException;

import com.ForgeEssentials.util.Localization;

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
