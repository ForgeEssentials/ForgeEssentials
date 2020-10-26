package com.forgeessentials.core.moduleLauncher;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is called during PreInitialization.
 * To Cancel loading of modules, please register to this event.
 */
@Cancelable
public class ModuleRegistrationEvent extends Event
{
    public ModuleContainer moduleContainer;
    public ModuleRegistrationEvent(ModuleContainer mC) {
        moduleContainer = mC;
    }
}
