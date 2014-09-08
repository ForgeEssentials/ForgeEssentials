package com.forgeessentials.util.events.modules;

import com.forgeessentials.core.moduleLauncher.ModuleContainer;

import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public class FEModuleServerStopEvent extends FEModuleEvent {
    private FMLServerStoppingEvent event;

    public FEModuleServerStopEvent(ModuleContainer container, FMLServerStoppingEvent event)
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
