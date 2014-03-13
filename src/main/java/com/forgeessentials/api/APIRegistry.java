package com.forgeessentials.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.IZoneManager;
import com.forgeessentials.api.snooper.Response;

import cpw.mods.fml.common.FMLLog;

/**
 * This is the central access point for all FE API functions
 * @author luacs1998
 *
 */
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
				ResponseRegistry_regsisterResponce = Class.forName("com.forgeessentials.snooper.ResponseRegistry").getMethod("registerResponse", Integer.class, Response.class);
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
	private static Method PacketAnalyzerRegistry_register;
	
	/**
	 * Register your packet analyzers here. No ID support.
	 * @param analyzer Your packet analyzer
	 */
	public static void registerPacketAnalyzer(IPacketAnalyzer analyzer){
		try
		{
			if (PacketAnalyzerRegistry_register == null)
			{
				PacketAnalyzerRegistry_register= Class.forName("com.forgeessentials.core.misc.PacketAnalyzerRegistry").getMethod("register", IPacketAnalyzer.class);
			}
			PacketAnalyzerRegistry_register.invoke(null, analyzer);
		}
		catch (Exception e)
		{
			FMLLog.warning("[FE API] Unable to register packet analyzer " + analyzer.getClass().toString());
			e.printStackTrace();
		}
	}
	/**
	 * Use this annotation to mark classes where static methods with other FE annotations might be.
	 * @author AbrarSyed
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	public @interface ForgeEssentialsRegistrar
	{
		String ident();

		/**
		 * Called before Pre-Init
		 * @param event IPermRegisterEvent
		 */
		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.METHOD })
		public @interface PermRegister
		{
		}
	}

}
