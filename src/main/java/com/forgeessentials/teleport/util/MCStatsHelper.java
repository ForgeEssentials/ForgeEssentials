package com.forgeessentials.teleport.util;

import com.forgeessentials.api.json.JSONException;
import com.forgeessentials.api.json.JSONObject;
import com.forgeessentials.core.compat.IServerStats;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;
import org.mcstats.Metrics.Plotter;

public class MCStatsHelper implements IServerStats {
    @Override
    public void makeGraphs(Metrics metrics)
    {
        Graph graph = metrics.createGraph("ModuleTeleport");

        Plotter plotter = new Plotter("Warps") {
            @Override
            public int getValue()
            {
                return TeleportDataManager.warps.size();
            }
        };

        graph.addPlotter(plotter);
    }

    @Override
    public JSONObject addToServerInfo() throws JSONException
    {
        JSONObject data = new JSONObject();
        data.put("Warps", TeleportDataManager.warps.size());
        return data;
    }

}