package com.ForgeEssentials.core.compat;

import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.lib.mcstats.Metrics;

public interface IServerStats
{
	public void makeGraphs(Metrics metrics);

	public JSONObject addToServerInfo() throws JSONException;
}
