package com.ForgeEssentials.core.moduleLauncher;

public abstract class FEModuleEvent
{

	ModuleContainer container;
	
	public FEModuleEvent(ModuleContainer container)
	{
		this.container = container;
	}
}
