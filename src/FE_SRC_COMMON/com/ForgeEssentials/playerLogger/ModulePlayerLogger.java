package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Config;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerStop;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePreInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerStopEvent;
import com.ForgeEssentials.playerLogger.types.blockChangeLog;
import com.ForgeEssentials.playerLogger.types.commandLog;
import com.ForgeEssentials.playerLogger.types.logEntry;
import com.ForgeEssentials.playerLogger.types.playerTrackerLog;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

@FEModule(name = "PlayerLogger", parentMod = ForgeEssentials.class, configClass = ConfigPlayerLogger.class)
public class ModulePlayerLogger
{
	static final boolean				DEBUG		= !ObfuscationReflectionHelper.obfuscation;

	@Config
	public static ConfigPlayerLogger	config;

	public static String				url;
	public static String				username;
	public static String				password;
	public static boolean				ragequitOn;
	public static Integer				interval;
	public static boolean				enable		= false;

	private Connection					connection;
	public static EventLogger			eLogger;

	public static List<logEntry>		logTypes	= new ArrayList();
	static
	{
		logTypes.add(new playerTrackerLog());
		logTypes.add(new commandLog());
		logTypes.add(new blockChangeLog());
	}

	public ModulePlayerLogger()
	{
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		if (!enable)
		{
			return;
		}
		OutputHandler.SOP("PlayerLogger module is enabled. Loading...");
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		if (!enable)
		{
			return;
		}
		try
		{
			Class mySQLclass = Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException error)
		{
			throw new RuntimeException("Could not find MySQL JDBC Driver.");
		}
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		if (!enable)
		{
			return;
		}
		e.registerServerCommand(new CommandPl());
		e.registerServerCommand(new CommandRollback());
		try
		{
			connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
			Statement s = connection.createStatement();

			/*
			 * if (DEBUG && false)
			 * {
			 * for (logEntry type : logTypes)
			 * {
			 * s.execute("DROP TABLE IF EXISTS " + type.getName());
			 * }
			 * }
			 */

			for (logEntry type : logTypes)
			{
				s.execute(type.getTableCreateSQL());
			}

			s.close();
			connection.close();
			eLogger = new EventLogger();
		}
		catch (SQLException e1)
		{
			OutputHandler.SOP("Could not connect to database! Wrong credentials?");
			OutputHandler.SOP(e1.getMessage());
			e1.printStackTrace();
		}
	}

	@ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		if (!enable)
		{
			return;
		}
		try
		{
			eLogger.logLoop.sendLogs();
			eLogger.logLoop.end();
		}
		catch (Exception ex)
		{
			OutputHandler.SOP("WARNING! MySQLConnector for playerLogger failed!");
		}
	}

	public static void ragequit()
	{
		if (ragequitOn)
		{
			FMLCommonHandler.instance().raiseException(new RuntimeException(), "Database connection lost.", true);
		}
	}

	public static void log(logEntry e)
	{
		eLogger.logLoop.buffer.add(e);
	}
}
