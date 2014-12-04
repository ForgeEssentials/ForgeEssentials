package com.forgeessentials.util.events;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventBus;

public class EventCancelledException extends Exception {

    private Event event;

    public EventCancelledException(Event event)
    {
        this.event = event;
    }

    public Event getEvent()
    {
        return event;
    }

    public static void checkedPost(Event e, EventBus eventBus) throws EventCancelledException
    {
        if (eventBus.post(e))
        {
            throw new EventCancelledException(e);
        }
    }

    public static void checkedPost(Event e) throws EventCancelledException
    {
        checkedPost(e, MinecraftForge.EVENT_BUS);
    }

}
