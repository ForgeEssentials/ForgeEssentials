package com.ForgeEssentials.playerLogger;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class ModulePlayerLogger implements IFEModule
{
	static final boolean DEBUG = !ObfuscationReflectionHelper.obfuscation;
	
	public static ConfigPlayerLogger config;
	
	public static String url;
	public static String username;
	public static String password;
	public static boolean ragequitOn;
	public static int interval;
	
	public EventLogger eLogger;
	
	public ModulePlayerLogger()
	{
		eLogger = new EventLogger();
	}

	@Override
	public void preLoad(FMLPreInitializationEvent e) 
	{
		OutputHandler.SOP("PlayerLogger module is enabled. Loading...");
		config = new ConfigPlayerLogger();
	}

	@Override
	public void load(FMLInitializationEvent e) 
	{
			
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e) 
	{
		
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e) 
	{
		try
		{
			MySQLConnector connector = new MySQLConnector();
		
			connector.makeTable();
			connector.close();
		
			eLogger.start();
		
			if(DEBUG)
			{
				OutputHandler.debug("Debug mode engaged.");
				for(int i = 0; i < 10; i++)
				{
					eLogger.logLoop.buffer.add(new logEntry("Test" + i, LogCatagory.DEBUG, "Disc " + i + ""));
				}
			}
		}
		catch (Exception ex)
		{
			OutputHandler.SOP("WARNING! MySQLConnector for playerLogger failed!");
		}
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e) 
	{
		
	}
	
	@Override
	public void serverStopping(FMLServerStoppingEvent e) 
	{
		try
		{
			//eLogger.logLoop.makeLogs();
			eLogger.logLoop.end();
		}
		catch (Exception ex)
		{
			OutputHandler.SOP("WARNING! MySQLConnector for playerLogger failed!");
		}
	}

	public static void ragequit() 
	{
		if(ragequitOn)
			FMLCommonHandler.instance().raiseException(new RuntimeException(), "Database connection lost.", true);
	}
}
