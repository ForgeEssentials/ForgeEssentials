package com.forgeessentials.core.compat;

import org.mcstats.Metrics;

import com.forgeessentials.api.json.JSONException;
import com.forgeessentials.api.json.JSONObject;

public interface IServerStats
{
	public void makeGraphs(Metrics metrics);

	public JSONObject addToServerInfo() throws JSONException;
}
