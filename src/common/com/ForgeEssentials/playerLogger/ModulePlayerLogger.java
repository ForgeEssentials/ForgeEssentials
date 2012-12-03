package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashSet;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModulePlayerLogger implements IFEModule
{
	static final boolean DEBUG = !ObfuscationReflectionHelper.obfuscation;
	
	public static ConfigPlayerLogger config;
	
	public static String url;
	public static String username;
	public static String password;
	public static boolean ragequitOn;
	public static int interval;
	public static boolean verbose;
	
	public EventLogger eLogger;
	
	public ModulePlayerLogger()
	{
		if (!ModuleLauncher.loggerEnabled)
			return;
		
		OutputHandler.debug("MYSQL logger enabled");
		
		eLogger = new EventLogger();
	}

	@Override
	public void preLoad(FMLPreInitializationEvent e) 
	{
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
		MySQLConnector connector = new MySQLConnector();
		if(!ModuleLauncher.loggerEnabled) return;
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

	@Override
	public void serverStarted(FMLServerStartedEvent e) 
	{
		
	}
	
	@Override
	public void serverStopping(FMLServerStoppingEvent e) 
	{
		//eLogger.logLoop.makeLogs();
		eLogger.logLoop.end();
	}

	public static void ragequit() 
	{
		if(ragequitOn)
			FMLCommonHandler.instance().raiseException(new RuntimeException(), "Database connection lost.", true);
	}
	
	public static void print(String msg)
	{
		if(verbose) OutputHandler.SOP(msg);
	}
}
