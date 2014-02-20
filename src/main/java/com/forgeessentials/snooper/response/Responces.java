package com.forgeessentials.snooper.response;

import net.minecraftforge.common.Configuration;

import com.forgeessentials.api.json.JSONArray;
import com.forgeessentials.api.json.JSONException;
import com.forgeessentials.api.json.JSONObject;
import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.snooper.ResponseRegistry;

public class Responces extends Response
{
	@Override
	public JSONObject getResponce(JSONObject input) throws JSONException
	{
		JSONArray data = new JSONArray();
		for (Response responce : ResponseRegistry.getAllresponses())
		{
			data.put(responce.id + " " + responce.getName());
		}

		return new JSONObject().put(getName(), data);
	}

	@Override
	public String getName()
	{
		return "Responces";
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
