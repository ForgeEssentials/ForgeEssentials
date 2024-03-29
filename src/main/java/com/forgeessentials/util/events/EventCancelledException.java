package com.forgeessentials.util.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventCancelledException extends Exception
{
    private static final long serialVersionUID = 6106472655247525969L;

    private Event event;

    public EventCancelledException(Event event)
    {
        this.event = event;
    }

    public Event getEvent()
    {
        return event;
    }

    public static void checkedPost(Event e, IEventBus eventBus) throws EventCancelledException
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
