package com.forgeessentials.util.events.modules;

import com.forgeessentials.core.moduleLauncher.ModuleContainer;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public class FEModulePostInitEvent extends FEModuleEvent {
    private FMLPostInitializationEvent event;

    public FEModulePostInitEvent(ModuleContainer container, FMLPostInitializationEvent event)
    {
        super(container);
        this.event = event;
    }

    @Override
    public FMLStateEvent getFMLEvent()
    {
        return event;
    }

    /**
     * bouncer for FML event method
     *
     * @param modId
     * @param className
     * @return
     */
    public Object buildSoftDependProxy(String modId, String className)
    {
        return event.buildSoftDependProxy(modId, className);
    }

}
