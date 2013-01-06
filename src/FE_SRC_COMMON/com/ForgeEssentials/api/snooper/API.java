package com.ForgeEssentials.api.snooper;

import java.lang.reflect.Method;

import cpw.mods.fml.common.FMLLog;

public class API 
{
	/**
	 * Use this method to register your responses. DON'T ID 9. That is used for challenge.
	 * @param ID
	 * @param response
	 */
	public static void registerResponce(Integer ID, Response response)
	{
		try
		{
			if (ResponseRegistry_regsisterResponce == null) ResponseRegistry_regsisterResponce = Class.forName("com.ForgeEssentials.snooper.ResponseRegistry").getMethod("registerResponse", Integer.class, Response.class);
			ResponseRegistry_regsisterResponce.invoke(null, ID, response);
		}
		catch(Exception e)
		{
			FMLLog.warning("Unable to register " + response.getName() + " with ID: " + ID);
			e.printStackTrace();
		}
	}
	
	private static Method ResponseRegistry_regsisterResponce;
}
