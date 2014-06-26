package com.forgeessentials.util.events.modules;

import com.forgeessentials.core.moduleLauncher.CallableMap;
import com.forgeessentials.core.moduleLauncher.ModuleContainer;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public class FEModulePreInitEvent extends FEModuleEvent {
    private FMLPreInitializationEvent event;
    private CallableMap callables;

    public FEModulePreInitEvent(ModuleContainer container, FMLPreInitializationEvent event, CallableMap map)
    {
        super(container);
        this.event = event;
        callables = map;
    }

    @Override
    public FMLStateEvent getFMLEvent()
    {
        return event;
    }

    public CallableMap getCallableMap()
    {
        return callables;
    }
}
