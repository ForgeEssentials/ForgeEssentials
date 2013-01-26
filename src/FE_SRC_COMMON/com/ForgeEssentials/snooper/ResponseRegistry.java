package com.ForgeEssentials.snooper;

import com.ForgeEssentials.api.snooper.Response;

import java.util.Collection;
import java.util.HashMap;

import cpw.mods.fml.common.FMLLog;

public class ResponseRegistry
{
	private static HashMap<Integer, Response> map = new HashMap<Integer, Response>();

	/**
	 * Register a response for an ID. Use the API!
	 * 
	 * @param ID
	 * @param response
	 */
	public static void registerResponse(Integer ID, Response response)
	{
		if (ID == 9)
		{
			return;
		}
		if (map.containsKey(ID))
		{
			throw new RuntimeException("You are attempting to register a response on an used ID: " + ID);
		}
		else
		{
			FMLLog.fine("Response " + response.getName() + " ID: " + ID + " registered!");
			map.put(ID, response);
		}
	}

	/**
	 * Used to build the response.
	 * 
	 * @param ID
	 * @return
	 */
	public static Response getResponse(byte ID)
	{
		if (map.containsKey((int) ID))
		{
			return map.get((int) ID);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Used by config
	 * 
	 * @return
	 */
	public static Collection<Response> getAllresponses()
	{
		return map.values();
	}
}
