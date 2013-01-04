package com.ForgeEssentials.playerLogger;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IModuleConfig;

import net.minecraft.command.ICommandSender;

import net.minecraftforge.common.Configuration;

import java.io.File;

public class ConfigPlayerLogger implements IModuleConfig
{
	public static final File	plconfig	= new File(ForgeEssentials.FEDIR, "playerlogger.cfg");
	public Configuration		config;

	@Override
	public void setGenerate(boolean generate)
	{
	}

	@Override
	public void init()
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

	@Override
	public void forceSave()
	{
		String cat = "playerLogger";
		String subcat = cat;

		ModulePlayerLogger.enable = config.get(subcat, "enable", false).getBoolean(false);

		subcat = cat + ".DB";
		config.addCustomCategoryComment(subcat, "Database settings. Look here if something broke.");

		config.get(subcat, "url", "jdbc:mysql://localhost:3306/testdb", "jdbc url").value = ModulePlayerLogger.url;
		config.get(subcat, "username", "root").value = ModulePlayerLogger.username;
		config.get(subcat, "password", "root").value = ModulePlayerLogger.password;
		config.get(subcat, "ragequit", false, "Stop the server when the logging fails").value = "" + ModulePlayerLogger.ragequitOn;
		config.get(subcat, "interval", 300, "Amount of time (in sec.) imbetween database saves.").value = "" + ModulePlayerLogger.interval;

		config.get(subcat, "blockchages", true).value = "" + EventLogger.logBlockChanges;
		config.get(subcat, "commands", true).value = "" + EventLogger.logCommands;
		config.get(subcat, "playerLoginLogout", true).value = "" + EventLogger.logPlayerLoginLogout;
		config.get(subcat, "playerChangeDimention", true).value = "" + EventLogger.logPlayerChangedDimension;
		config.get(subcat, "playerRespawn", true).value = "" + EventLogger.logPlayerRespawn;

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		String cat = "playerLogger";

		ModulePlayerLogger.enable = config.get(cat, "enable", false).getBoolean(false);

		cat = cat + ".DB";
		ModulePlayerLogger.url = config.get(cat, "url", "jdbc:mysql://localhost:3306/testdb", "jdbc url").value;
		ModulePlayerLogger.username = config.get(cat, "username", "root").value;
		ModulePlayerLogger.password = config.get(cat, "password", "root").value;
		ModulePlayerLogger.ragequitOn = config.get(cat, "ragequit", false, "Stop the server when the logging fails").getBoolean(false);
		ModulePlayerLogger.interval = config.get(cat, "interval", 300, "Amount of time (in sec.) imbetween database saves.").getInt();

		EventLogger.logBlockChanges = config.get(cat, "blockchages", true).getBoolean(true);
		EventLogger.logCommands = config.get(cat, "commands", true).getBoolean(true);
		EventLogger.logPlayerLoginLogout = config.get(cat, "playerLoginLogout", true).getBoolean(true);
		EventLogger.logPlayerChangedDimension = config.get(cat, "playerChangeDimention", true).getBoolean(true);
		EventLogger.logPlayerRespawn = config.get(cat, "playerRespawn", true).getBoolean(true);
	}

	@Override
	public File getFile()
	{
		return plconfig;
	}
}
