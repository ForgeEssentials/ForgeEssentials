package com.forgeessentials.util.events;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.Event;

public class FERegisterCommandsEvent extends Event
{
    protected RegisterCommandsEvent event;

    public FERegisterCommandsEvent(RegisterCommandsEvent event)
    {
        this.event = event;
    }

    public RegisterCommandsEvent getRegisterCommandsEvent()
    {
        return event;
    }
}