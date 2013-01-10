package com.ForgeEssentials.core.moduleLauncher.event;

import com.ForgeEssentials.core.moduleLauncher.ModuleContainer;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public class FEModulePostInitEvent extends FEModuleEvent
{
	private FMLPostInitializationEvent event;

	public FEModulePostInitEvent(ModuleContainer container, FMLPostInitializationEvent event)
	{
		super(container);
		this.event = event;
	}

	@Override
	public FMLStateEvent getFEEvent()
	{
		return event;
	}
	
	/**
	 * bouncer for FML event method
	 * @param modId
	 * @param className
	 * @return
	 */
    public Object buildSoftDependProxy(String modId, String className)
    {
    	return event.buildSoftDependProxy(modId, className);
    }

}
