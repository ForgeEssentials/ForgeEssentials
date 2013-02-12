package com.ForgeEssentials.core.compat;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;
import org.mcstats.Metrics.Plotter;

import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.ModuleLauncher;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

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
	
	public static boolean isOnlineMode(){
		return MinecraftServer.getServer().isServerInOnlineMode();
	}
	
	public static boolean isDediServer(){
		return MinecraftServer.getServer().isDedicatedServer();
	}
	
	public static int getPlayers(){
		return MinecraftServer.getServer().getCurrentPlayerCount();
	}
	public static String getMCVer(){
		return MinecraftServer.getServer().getMinecraftVersion();
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
