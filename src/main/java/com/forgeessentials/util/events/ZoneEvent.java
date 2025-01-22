package com.forgeessentials.util.events;

import com.forgeessentials.api.permissions.Zone;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ZoneEvent extends Event
{

    protected static Zone zone;

    public static class Create extends ZoneEvent
    {
        public Create(Zone created)
        {
            zone = created;
        }
    }

    public static class Delete extends ZoneEvent
    {
        public Delete(Zone toDelete)
        {
            zone = toDelete;
        }
    }

    public Zone getZone()
    {
        return zone;
    }
}
