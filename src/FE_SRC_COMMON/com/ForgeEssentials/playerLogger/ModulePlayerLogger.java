package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.IModuleConfig;
import com.ForgeEssentials.playerLogger.types.blockChangeLog;
import com.ForgeEssentials.playerLogger.types.commandLog;
import com.ForgeEssentials.playerLogger.types.logEntry;
import com.ForgeEssentials.playerLogger.types.playerTrackerLog;
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
	public static Integer interval;
	public static boolean enable = false;

	private Connection connection;
	public static EventLogger eLogger;

	public static List<logEntry> logTypes = new ArrayList();
	static
	{
		logTypes.add(new playerTrackerLog());
		logTypes.add(new commandLog());
		logTypes.add(new blockChangeLog());
	}

	public ModulePlayerLogger()
	{
		if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			return;
		}
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@Override
	public void preLoad(FMLPreInitializationEvent e)
	{
		if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			return;
		}
		config = new ConfigPlayerLogger();
		if (!enable)
		{
			return;
		}
		OutputHandler.SOP("PlayerLogger module is enabled. Loading...");
	}

	@Override
	public void load(FMLInitializationEvent e)
	{
		if (!enable)
		{
			return;
		}
		try
		{
			Class mySQLclass = Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException error)
		{
			throw new RuntimeException("Could not find MySQL JDBC Driver.");
		}
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{

	}

	@Override
	public void serverStarting(FMLServerStartingEvent e)
	{
		if (!enable)
		{
			return;
		}
		e.registerServerCommand(new CommandPl());
		try
		{
			connection = DriverManager.getConnection(ModulePlayerLogger.url,
					ModulePlayerLogger.username, ModulePlayerLogger.password);
			Statement s = connection.createStatement();

			if (DEBUG && false)
			{
				for (logEntry type : logTypes)
				{
					s.execute("DROP TABLE IF EXISTS " + type.getName());
				}
			}

			for (logEntry type : logTypes)
			{
				s.execute(type.getTableCreateSQL());
			}

			s.close();
			connection.close();
			eLogger = new EventLogger();
		} catch (SQLException e1)
		{
			OutputHandler
					.SOP("Could not connect to database! Wrong credentials?");
			OutputHandler.SOP(e1.getMessage());
			e1.printStackTrace();
		}
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{

	}

	@Override
	public void serverStopping(FMLServerStoppingEvent e)
	{
		if (!enable)
		{
			return;
		}
		try
		{
			eLogger.logLoop.sendLogs();
			eLogger.logLoop.end();
		} catch (Exception ex)
		{
			OutputHandler
					.SOP("WARNING! MySQLConnector for playerLogger failed!");
		}
	}

	public static void ragequit()
	{
		if (ragequitOn)
		{
			FMLCommonHandler.instance().raiseException(new RuntimeException(),
					"Database connection lost.", true);
		}
	}

	@Override
	public IModuleConfig getConfig()
	{
		return config;
	}
}
