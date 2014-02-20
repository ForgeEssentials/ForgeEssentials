package com.forgeessentials.util.events.modules;

import java.io.File;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.ModuleContainer;

import cpw.mods.fml.common.event.FMLStateEvent;

public abstract class FEModuleEvent
{
	protected ModuleContainer	container;

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
