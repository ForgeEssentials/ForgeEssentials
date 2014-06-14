package com.forgeessentials.commands.util;

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
        Graph graph = metrics.createGraph("ModuleCommands");

        Plotter plotter = new Plotter("Kits") {
            @Override
            public int getValue()
            {
                return CommandDataManager.kits.size();
            }
        };

        graph.addPlotter(plotter);
    }

    @Override
    public JSONObject addToServerInfo() throws JSONException
    {
        JSONObject data = new JSONObject();
        data.put("Kits", CommandDataManager.kits.size());
        return data;
    }

}
