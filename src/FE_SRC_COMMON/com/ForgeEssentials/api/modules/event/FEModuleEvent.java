package com.ForgeEssentials.api.modules.event;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.ModuleContainer;

import java.io.File;

import cpw.mods.fml.common.event.FMLStateEvent;

public abstract class FEModuleEvent
{

	protected ModuleContainer container;
	
	public FEModuleEvent(ModuleContainer container)
	{
		this.container = container;
	}
	
	public ModuleContainer getModuleContainer()
	{
		return container;
	}
	
	public File getModuleDir()
	{
		return new File(ForgeEssentials.FEDIR, container.name);
	}
	
	public abstract FMLStateEvent getFMLEvent();
}
