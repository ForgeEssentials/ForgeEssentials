package com.ForgeEssentials.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.mcstats.Metrics;

import com.ForgeEssentials.api.IServerStats;

public class ServerStats
{
	private static List<IServerStats> handlers = new ArrayList();
	private static Metrics metrics;
	
	public static void registerStats(IServerStats generator)
	{
		handlers.add(generator);
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
}
