package com.ForgeEssentials.core.compat;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.mcstats.Metrics;

import com.ForgeEssentials.api.IServerStats;
import com.ForgeEssentials.core.ForgeEssentials;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

// Obfuscated code handler for MCStats
public class CompatMCStats
{
	public void initConfig(){
		Configuration configuration;
		configuration = new Configuration(new File(ForgeEssentials.FEDIR, "mcstats.cfg"));

        // Get values, and add some defaults, if needed
        configuration.get(Configuration.CATEGORY_GENERAL, "opt-out", false,
                "Set to true to disable all reporting");
        Metrics.guid = configuration.get(Configuration.CATEGORY_GENERAL, "guid", UUID
                .randomUUID().toString(), "Server unique ID").value;
    
        configuration.save();
	}
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
	public static boolean isOnlineMode(){
		return MinecraftServer.getServer().isServerInOnlineMode();
	}
	public static boolean isDediServer(){
		return MinecraftServer.getServer().isDedicatedServer();
	}
	public static int getPlayers(){
		return MinecraftServer.getServer().getCurrentPlayerCount();
	}

}
