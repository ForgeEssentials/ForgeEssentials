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

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class ModulePlayerLogger implements IFEModule
{
	private static final boolean DEBUG = true;
	public static String url;
	public static String username;
	public static String password;
	public static boolean ragequit;
	public static ConfigPlayerLogger config;
	
	public static int interval; 
	
	public static HashSet<logEntry> buffer = new HashSet<logEntry> ();
	private Logger logger;
	private Thread loggerThread;
	
	public ModulePlayerLogger()
	{
		if (!ModuleLauncher.loggerEnabled)
			return;
		
		OutputHandler.debug("MYSQL logger enabled");
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
		connector.makeTable();
		connector.close();
		logger = new Logger();
		new Thread(logger, "MySQL Connection Thread - PlayerLogger").start();
		
		if(DEBUG)
		{
			OutputHandler.debug("Debug mods engaged.");
			for(int i = 0; i < 10; i++)
			{
				buffer.add(new logEntry("Test" + i, Calendar.getInstance().getTime().toGMTString(), "CatTest" + i, "Disc " + i + " loc: X Y Z"));
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
		logger.makeLogs();
		logger.end();
	}
	
	public class logEntry
	{
		public String player;
		public String time;
		public String category;
		public String disciption;
		
		public logEntry(String player, String time, String category, String disciption)
		{
			OutputHandler.debug("new logEntry("+player+")");
			this.player = player;
			this.time = time;
			this.category = category;
			this.disciption = disciption;
		}
	}
}
