package com.ForgeEssentials.core.compat;

import java.util.LinkedHashMap;

import org.mcstats.Metrics;

public interface IServerStats
{
	public void makeGraphs(Metrics metrics);
	
	public LinkedHashMap<String, String> addToServerInfo();
}
