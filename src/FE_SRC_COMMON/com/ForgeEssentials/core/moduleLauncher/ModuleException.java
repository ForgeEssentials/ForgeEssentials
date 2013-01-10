package com.ForgeEssentials.core.moduleLauncher;

public class ModuleException extends Exception
{
	ModuleContainer module;
	
	public ModuleException(ModuleContainer m)
	{
		module = m;
	}
	
	public ModuleException(String message, ModuleContainer m)
	{
		super(message);
		module = m;
	}
}
