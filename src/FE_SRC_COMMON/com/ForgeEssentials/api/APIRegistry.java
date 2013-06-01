package com.ForgeEssentials.api;

import java.lang.reflect.Method;

import com.ForgeEssentials.api.permissions.IPermissionsHelper;
import com.ForgeEssentials.api.permissions.IZoneManager;
import com.ForgeEssentials.api.snooper.Response;

import cpw.mods.fml.common.FMLLog;

public class APIRegistry {
	
	// Use this to call API functions available in the economy module.
	public static IEconManager	wallet;
	
	// Use to call API functions from the permissions module.
	public static IPermissionsHelper perms;
	
	// Use to access the zone manager.
	public static IZoneManager zones;
	
	/**
	 * Snooper method to register your responses.
	 * @param ID
	 * @param response
	 */
	public static void registerResponse(Integer ID, Response response)
	{
		try
		{
			if (ResponseRegistry_regsisterResponce == null)
			{
				ResponseRegistry_regsisterResponce = Class.forName("com.ForgeEssentials.snooper.ResponseRegistry").getMethod("registerResponse", Integer.class, Response.class);
			}
			ResponseRegistry_regsisterResponce.invoke(null, ID, response);
		}
		catch (Exception e)
		{
			FMLLog.warning("[FE API] Unable to register " + response.getName() + " with ID: " + ID);
			e.printStackTrace();
		}
	}

	private static Method	ResponseRegistry_regsisterResponce;

}
