package com.ForgeEssentials.snooper.response;

import java.net.DatagramPacket;
import java.util.LinkedHashMap;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.core.compat.CompatMCStats;

public class MCstatsInfo extends Response
{
	LinkedHashMap<String, String>	data		= new LinkedHashMap();
	
	@Override
	public String getResponceString(DatagramPacket packet)
	{
		data.putAll(CompatMCStats.doSnooperStats());
		return dataString = TextFormatter.toJSON(data);
	}

	@Override
	public String getName()
	{
		return "CustomInfo";
	}

	@Override
	public void readConfig(String category, Configuration config)
	{
		
	}

	@Override
	public void writeConfig(String category, Configuration config)
	{
		
	}

}
