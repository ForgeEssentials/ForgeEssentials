package com.ForgeEssentials.core.compat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.ModuleLauncher;
import com.ForgeEssentials.lib.mcstats.Metrics;
import com.ForgeEssentials.lib.mcstats.Metrics.Graph;
import com.ForgeEssentials.lib.mcstats.Metrics.Plotter;

// Obfuscated code handler for MCStats
public class CompatMCStats implements IServerStats
{
	public void load(){
		registerStats(this);
	}
	private static List<IServerStats> handlers = new ArrayList();
	private static Metrics metrics;
	
	public static void registerStats(IServerStats generator)
	{
		if(generator != null)
		{
			handlers.add(generator);
		}
		else
		{
			throw new RuntimeException("Why would you register null?");
		}
	}
	
	public static void doMCStats()
	{
		try
		{
			metrics = new Metrics("ForgeEssentials", ForgeEssentials.version);
			
			for(IServerStats obj : handlers)
			{
				obj.makeGraphs(metrics);
			}
			
			if(ForgeEssentials.mcstats)
			{
				metrics.start();
			}
		}
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	public static LinkedHashMap<String, String> doSnooperStats()
	{
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
		for(IServerStats obj : handlers)
		{
			LinkedHashMap<String, String> temp = obj.addToServerInfo();
			if(map != null)
			{
				map.putAll(temp);
			}
		}
		
		return map;
	}
	
	@Override
	public void makeGraphs(Metrics metrics)
	{
		Graph graph = metrics.createGraph("Modules used");
		for(String module : ModuleLauncher.getModuleList())
		{
			Plotter plotter = new Plotter(module)
			{
				@Override
				public int getValue()
				{
					return 1;
				}
			};
			graph.addPlotter(plotter);
		}
	}

	@Override
	public LinkedHashMap<String, String> addToServerInfo()
	{
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("FEmodules", TextFormatter.toJSON(ModuleLauncher.getModuleList()));
		return map;
	}

}
