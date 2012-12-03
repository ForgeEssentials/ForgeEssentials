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
		config.addCustomCategoryComment("PlayerLogger", "PlayerLogger settings");

		Property prop = config.get("PlayerLogger", "DB_url", "jdbc:mysql://localhost:3306/testdb");
		ModulePlayerLogger.url = prop.value;
		
		prop = config.get("PlayerLogger", "DB_username", "root");
		ModulePlayerLogger.username = prop.value;
		
		prop = config.get("PlayerLogger", "DB_password", "root");
		ModulePlayerLogger.password = prop.value;
		
		prop = config.get("PlayerLogger", "stopServerIfFail", false);
		prop.comment = "Stop the server when the logging fails";
		ModulePlayerLogger.ragequit = prop.getBoolean(false);
		
		prop = config.get("PlayerLogger", "interval", 300);
		prop.comment = "Interval in sec. for saving logs to DB";
		ModulePlayerLogger.interval = prop.getInt(300);
		config.save();
	}
		
}
