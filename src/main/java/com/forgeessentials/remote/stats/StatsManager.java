package com.forgeessentials.remote.stats;

import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class StatsManager extends ServerEventHandler
{

    protected static final Map<String, StatTracker<?>> stats = new HashMap<>();

    public StatsManager()
    {
        addStatTracker("ram", new StatTracker<Long>(10, 60 * 60) {
            @Override
            public Long getValue()
            {
                return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
            }
        });
        addStatTracker("playercount", new StatTracker<Integer>(60 * 2, 60 * 60 * 24) {
            @Override
            public Integer getValue()
            {
                return ServerUtil.getPlayerList().size();
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
