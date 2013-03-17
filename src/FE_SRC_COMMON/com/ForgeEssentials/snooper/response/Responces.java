package com.ForgeEssentials.snooper.response;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.json.JSONArray;
import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.snooper.ResponseRegistry;

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
