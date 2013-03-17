package com.ForgeEssentials.commands.util;

import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.core.compat.IServerStats;
import com.ForgeEssentials.lib.mcstats.Metrics;
import com.ForgeEssentials.lib.mcstats.Metrics.Graph;
import com.ForgeEssentials.lib.mcstats.Metrics.Plotter;

public class MCStatsHelper implements IServerStats
{
	@Override
	public void makeGraphs(Metrics metrics)
	{
		Graph graph = metrics.createGraph("ModuleCommands");

		Plotter plotter = new Plotter("Warps")
		{
			@Override
			public int getValue()
			{
				return CommandDataManager.warps.size();
			}
		};

		plotter = new Plotter("Kits")
		{
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
		data.put("Warps", CommandDataManager.warps.size());
		data.put("Kits", CommandDataManager.kits.size());
		return data;
	}

}
