package com.ForgeEssentials.playerLogger;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigPlayerLogger {
	public static final File plconfig = new File(ForgeEssentials.FEDIR, "playerlogger.cfg");
	public final Configuration config;
	
	public ConfigPlayerLogger()
	{
		config = new Configuration(plconfig, true);
		
		//DB settings
		config.addCustomCategoryComment("PlayerLogger_DB", "Database settings");

		Property prop = config.get("PlayerLogger_DB", "DB_url", "jdbc:mysql://localhost:3306/testdb");
		ModulePlayerLogger.url = prop.value;
		
		prop = config.get("PlayerLogger_DB", "DB_username", "root");
		ModulePlayerLogger.username = prop.value;
		
		prop = config.get("PlayerLogger_DB", "DB_password", "root");
		ModulePlayerLogger.password = prop.value;
		
		prop = config.get("PlayerLogger_DB", "stopServerIfFail", false);
		prop.comment = "Stop the server when the logging fails";
		ModulePlayerLogger.ragequitOn = prop.getBoolean(false);
		
		prop = config.get("PlayerLogger_DB", "verbose", true);
		prop.comment = "Causes console spamm but its handy to test out settings";
		ModulePlayerLogger.verbose = prop.getBoolean(true);
		
		prop = config.get("PlayerLogger_DB", "interval", 300);
		prop.comment = "Interval in sec. for saving logs to DB";
		ModulePlayerLogger.interval = prop.getInt(300);
		
		
		// Event settings
		config.addCustomCategoryComment("PlayerLogger_events", "What events do I need to log?");
		
		prop = config.get("PlayerLogger_events", "logPlayerLogin", true);
		EventLogger.logPlayerLogin = prop.getBoolean(true);
		
		prop = config.get("PlayerLogger_events", "logPlayerChangedDimension", true);
		EventLogger.logPlayerChangedDimension = prop.getBoolean(true);
		
		prop = config.get("PlayerLogger_events", "logPlayerLogout", true);
		EventLogger.logPlayerLogout = prop.getBoolean(true);
		
		prop = config.get("PlayerLogger_events", "logPlayerRespawn", true);
		EventLogger.logPlayerRespawn = prop.getBoolean(true);
		
		prop = config.get("PlayerLogger_events", "logCommands", true);
		EventLogger.logCommands = prop.getBoolean(true);
		
		config.save();
	}

}
