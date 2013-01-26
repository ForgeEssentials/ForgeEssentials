package com.ForgeEssentials.api.modules.event;

import com.ForgeEssentials.core.moduleLauncher.ModuleContainer;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public class FEModuleServerPostInitEvent extends FEModuleEvent
{
	private FMLServerStartedEvent event;

	public FEModuleServerPostInitEvent(ModuleContainer container, FMLServerStartedEvent event)
	{
		super(container);
		this.event = event;
	}

	@Override
	public FMLStateEvent getFMLEvent()
	{
		return event;
	}

}
