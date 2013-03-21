package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.PreInit;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.FEModule.ServerStop;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;
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

	@FEModule.Config
	public static ConfigPlayerLogger	config;

	public static String				url;
	public static String				username;
	public static String				password;
	public static boolean				ragequitOn;
	public static Integer				interval;
	public static boolean				enable		= false;

	private Connection					connection;
	public static EventLogger			eLogger;

	public static List<logEntry>		logTypes	= new ArrayList<logEntry>();
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
			return;
		OutputHandler.info("PlayerLogger module is enabled. Loading...");
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		for (String name : EventLogger.exempt_groups)
		{
			if (PermissionsAPI.getGroupForName(name) == null)
				throw new RuntimeException("Group '" + name + "' doesn't exist. Used in " + config.getFile().getName());
		}
		
		if (!enable)
			return;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
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
			return;
		e.registerServerCommand(new CommandPl());
		e.registerServerCommand(new CommandRollback());
		try
		{
			connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
			Statement s = connection.createStatement();

			/*
			 * if (DEBUG && false) { for (logEntry type : logTypes) {
			 * s.execute("DROP TABLE IF EXISTS " + type.getName()); } }
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
			OutputHandler.info("Could not connect to database! Wrong credentials?");
			OutputHandler.info(e1.getMessage());
			e1.printStackTrace();
		}
	}

	@ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		if (!enable)
			return;
		try
		{
			eLogger.logLoop.sendLogs();
			eLogger.logLoop.end();
		}
		catch (Exception ex)
		{
			OutputHandler.info("WARNING! MySQLConnector for playerLogger failed!");
			ex.printStackTrace();
		}
	}

	@PermRegister
	public static void registerPerms(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.playerLogger.rollback", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.playerLogger.playerlogger", RegGroup.OWNERS);

		// TODO : pending review from Dries.
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
