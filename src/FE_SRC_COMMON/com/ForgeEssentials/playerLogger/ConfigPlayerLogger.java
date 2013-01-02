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
		
		String cat = "playerLogger";
		String subcat = cat;
		
		ModulePlayerLogger.enable = config.get(subcat, "enable", false).getBoolean(false);
		
		subcat = cat + ".DB";
		config.addCustomCategoryComment(subcat, "Database settings. Look here if something broke.");
		
		ModulePlayerLogger.url = config.get(subcat, "url", "jdbc:mysql://localhost:3306/testdb", "jdbc url").value;
		ModulePlayerLogger.username = config.get(subcat, "username", "root").value;
		ModulePlayerLogger.password = config.get(subcat, "password", "root").value;
		ModulePlayerLogger.ragequitOn = config.get(subcat, "ragequit", false, "Stop the server when the logging fails").getBoolean(false);
		ModulePlayerLogger.interval = config.get(subcat, "interval", 300, "Amount of time (in sec.) imbetween database saves.").getInt();

		subcat = cat + ".events";
		config.addCustomCategoryComment("events", "Toggle events to log here.");
		
		EventLogger.logBlockChanges = config.get(subcat, "blockchages", true).getBoolean(true);
		EventLogger.logCommands = config.get(subcat, "commands", true).getBoolean(true);
		EventLogger.logPlayerLoginLogout = config.get(subcat, "playerLoginLogout", true).getBoolean(true);
		EventLogger.logPlayerChangedDimension = config.get(subcat, "playerChangeDimention", true).getBoolean(true);
		EventLogger.logPlayerRespawn = config.get(subcat, "playerRespawn", true).getBoolean(true);
		
		config.save();
	}

}
