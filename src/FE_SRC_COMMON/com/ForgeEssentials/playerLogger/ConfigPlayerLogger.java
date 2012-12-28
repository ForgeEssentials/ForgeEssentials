package com.ForgeEssentials.playerLogger;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;

public class ConfigPlayerLogger {
	public static final File plconfig = new File(ForgeEssentials.FEDIR, "playerlogger.cfg");
	public final Configuration config;
	
	public ConfigPlayerLogger()
	{
		config = new Configuration(plconfig, true);
		
		//DB settings
		config.addCustomCategoryComment("DB", "Database settings. Look here if something broke.");

		Property prop = config.get("DB", "DB_url", "jdbc:mysql://localhost:3306/testdb");
		prop.comment = "URL of the database";
		ModulePlayerLogger.url = prop.value;
		
		prop = config.get("DB", "DB_username", "root");
		prop.comment = "Username used to log in to the database";
		ModulePlayerLogger.username = prop.value;
		
		prop = config.get("DB", "DB_password", "root");
		prop.comment = "Password used to log in to the database";
		ModulePlayerLogger.password = prop.value;
		
		prop = config.get("DB", "stopServerIfFail", false);
		prop.comment = "Stop the server when the logging fails";
		ModulePlayerLogger.ragequitOn = prop.getBoolean(false);
		
		prop = config.get("DB", "interval", 300);
		prop.comment = "Interval in sec. for saving logs to DB";
		ModulePlayerLogger.interval = prop.getInt(300);
		
		
		// Event settings
		config.addCustomCategoryComment("events", "Toggle events to log here.");
		
		prop = config.get("events", "logPlayerLogin", true);
		prop.comment = "Log player logins?";
		EventLogger.logPlayerLogin = prop.getBoolean(true);
		
		prop = config.get("events", "logPlayerChangedDimension", true);
		prop.comment = "Log player dimension changes?";
		EventLogger.logPlayerChangedDimension = prop.getBoolean(true);
		
		prop = config.get("events", "logPlayerLogout", true);
		prop.comment = "Log player logouts?";
		EventLogger.logPlayerLogout = prop.getBoolean(true);
		
		prop = config.get("events", "logPlayerRespawn", true);
		prop.comment = "Log player respawning?";
		EventLogger.logPlayerRespawn = prop.getBoolean(true);
		
		prop = config.get("events", "logCommands", true);
		prop.comment = "Log commands?";
		EventLogger.logCommands = prop.getBoolean(true);
		
		config.save();
	}

}
