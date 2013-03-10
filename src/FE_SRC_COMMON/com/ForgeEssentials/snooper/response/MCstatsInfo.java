package com.ForgeEssentials.snooper.response;

import java.util.LinkedHashMap;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.core.compat.CompatMCStats;

public class MCstatsInfo extends Response
{
	LinkedHashMap<String, String>	data	= new LinkedHashMap<String, String>();
	
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

	@Override
	public JSONObject getResponce(String input) throws JSONException
	{
		return new JSONObject().put(this.getName(), CompatMCStats.doSnooperStats());
	}

}
