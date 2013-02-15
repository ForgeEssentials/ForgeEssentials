package com.ForgeEssentials.core.compat;

import java.util.LinkedHashMap;

import com.ForgeEssentials.lib.mcstats.Metrics;

public interface IServerStats
{
	public void makeGraphs(Metrics metrics);
	
	public LinkedHashMap<String, String> addToServerInfo();
}
