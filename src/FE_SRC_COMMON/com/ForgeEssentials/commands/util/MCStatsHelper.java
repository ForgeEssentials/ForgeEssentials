package com.ForgeEssentials.commands.util;

import java.util.LinkedHashMap;

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
	public LinkedHashMap<String, String> addToServerInfo()
	{
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Warps", "" + CommandDataManager.warps.size());
		map.put("Kits", "" + CommandDataManager.kits.size());
		return map;
	}

}
