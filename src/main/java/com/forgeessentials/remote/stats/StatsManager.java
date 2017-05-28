package com.forgeessentials.remote.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.ServerEventHandler;

public class StatsManager extends ServerEventHandler
{

    protected static final Map<String, StatTracker<?>> stats = new HashMap<>();

    protected static Timer timer = new Timer();

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

    public static void addStatTracker(String id, StatTracker<?> tracker)
    {
        if (stats.containsKey(id))
            throw new RuntimeException("Duplicate stat tracker ID used");
        stats.put(id, tracker);
        timer.scheduleAtFixedRate(tracker, tracker.getInterval(), tracker.getInterval());
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
