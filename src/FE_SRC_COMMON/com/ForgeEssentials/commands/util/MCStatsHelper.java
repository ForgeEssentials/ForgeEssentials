package com.ForgeEssentials.commands.util;

import java.util.LinkedHashMap;

import com.ForgeEssentials.core.compat.IServerStats;
import com.ForgeEssentials.lib.mcstats.Metrics;
import com.ForgeEssentials.lib.mcstats.Metrics.Graph;
import com.ForgeEssentials.lib.mcstats.Metrics.Plotter;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.TeleportCenter;

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
				return TeleportCenter.warps.size();
			}
		};

		plotter = new Plotter("Kits")
		{
			@Override
			public int getValue()
			{
				return DataStorage.getData("kitdata").getTags().size();
			}
		};

		graph.addPlotter(plotter);
	}

	@Override
	public LinkedHashMap<String, String> addToServerInfo()
	{
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("Warps", "" + TeleportCenter.warps.size());
		map.put("Kits", "" + DataStorage.getData("kitdata").getTags().size());
		return map;
	}

}
