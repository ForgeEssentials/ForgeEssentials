package com.forgeessentials.remote.stats;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import com.forgeessentials.util.events.ServerEventHandler;

public class StatsManager extends ServerEventHandler
{

    protected static final Map<String, StatTracker<?>> stats = new HashMap<>();

    public StatsManager()
    {
        addStatTracker("ram", new StatTracker<Long>(5, 60 * 60) {
            @Override
            public Long getValue()
            {
                return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void serverTickEvent(ServerTickEvent event)
    {
        for (StatTracker<?> tracker : stats.values())
            tracker.tick();
    }

    public static void addStatTracker(String id, StatTracker<?> tacker)
    {
        if (stats.containsKey(id))
            throw new RuntimeException("Duplicate stat tracker ID used");
        stats.put(id, tacker);
    }

    public static Map<String, StatTracker<?>> getStats()
    {
        return stats;
    }

    public StatTracker<?> getStat(String id)
    {
        return stats.get(id);
    }

}
