package com.ForgeEssentials.playerLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.IModuleConfig;

public class ConfigPlayerLogger implements IModuleConfig
{
	public static final File plconfig = new File(ForgeEssentials.FEDIR, "playerlogger.cfg");
	public Configuration config;

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
		config.addCustomCategoryComment(subcat, "Toggle events to log here.");

		EventLogger.logPlayerLoginLogout = config.get(subcat, "playerLoginLogout", true).getBoolean(true);
		EventLogger.logPlayerChangedDimension = config.get(subcat, "playerChangeDimention", true).getBoolean(true);
		EventLogger.logPlayerRespawn = config.get(subcat, "playerRespawn", true).getBoolean(true);

		String subcat2 = subcat + ".commands";
		
		EventLogger.logCommands_Player = config.get(subcat2, "Enable_Player", true).getBoolean(true);
		EventLogger.logCommands_Block = config.get(subcat2, "Enable_CmdBlock", true).getBoolean(true);
		EventLogger.logCommands_rest = config.get(subcat2, "Enable_Rest", true).getBoolean(true);
		
		subcat2 = subcat + ".blockChanges";
		
		EventLogger.logBlockChanges = config.get(subcat2, "Enable", true).getBoolean(true);
		EventLogger.BlockChange_WhiteList_Use = config.get(subcat2, "UseWhitelist", false, "If true: Only log in dimentions that are in the whitelist.").getBoolean(false);
		
		int[] intArray1 = config.get(subcat2, "WhiteList", new int[] {0, 1, -1}, "WhiteList overrides blacklist!").getIntList();
		for(int i : intArray1) EventLogger.BlockChange_WhiteList.add(i);
		
		int[] intArray2 = config.get(subcat2, "BlackList", new int[] {}, "Don't make logs in these dimentions.").getIntList();
		for(int i : intArray2) EventLogger.BlockChange_BlackList.add(i);
		
		config.save();
	}

	@Override
	public void forceSave()
	{
	}

	@Override
	public void forceLoad(ICommandSender sender)
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
		config.addCustomCategoryComment(subcat, "Toggle events to log here.");

		EventLogger.logPlayerLoginLogout = config.get(subcat, "playerLoginLogout", true).getBoolean(true);
		EventLogger.logPlayerChangedDimension = config.get(subcat, "playerChangeDimention", true).getBoolean(true);
		EventLogger.logPlayerRespawn = config.get(subcat, "playerRespawn", true).getBoolean(true);

		String subcat2 = subcat + ".commands";
		
		EventLogger.logCommands_Player = config.get(subcat2, "Enable_Player", true).getBoolean(true);
		EventLogger.logCommands_Block = config.get(subcat2, "Enable_CmdBlock", true).getBoolean(true);
		EventLogger.logCommands_rest = config.get(subcat2, "Enable_Rest", true).getBoolean(true);
		
		subcat2 = subcat + ".blockChanges";
		
		EventLogger.logBlockChanges = config.get(subcat2, "Enable", true).getBoolean(true);
		EventLogger.BlockChange_WhiteList_Use = config.get(subcat2, "UseWhitelist", false, "If true: Only log in dimentions that are in the whitelist.").getBoolean(false);
		
		int[] intArray1 = config.get(subcat2, "WhiteList", new int[] {0, 1, -1}, "WhiteList overrides blacklist!").getIntList();
		for(int i : intArray1) EventLogger.BlockChange_WhiteList.add(i);
		
		int[] intArray2 = config.get(subcat2, "BlackList", new int[] {}, "Don't make logs in these dimentions.").getIntList();
		for(int i : intArray2) EventLogger.BlockChange_BlackList.add(i);
		config.save();
	}

	@Override
	public File getFile()
	{
		return plconfig;
	}

}
