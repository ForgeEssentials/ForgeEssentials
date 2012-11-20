package com.ForgeEssentials.core.config;

import java.io.File;

import com.ForgeEssentials.api.permissions.Permission;
import com.ForgeEssentials.commands.CommandMotd;
import com.ForgeEssentials.commands.CommandRules;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.permissions.ModulePermissions;
import com.ForgeEssentials.util.OutputHandler;

public class FEConfig
{
	public static final File	FECONFIG	= new File(ForgeEssentials.FEDIR, "config.cfg");

	public final Configuration	config;

	// this is designed so it will work for any class.
	public FEConfig()
	{
		OutputHandler.SOP("Loading configs");
		config = new Configuration(FECONFIG, true);
		// config.load -- Configurations are loaded on Construction.

		// load the modules
		loadModules();
		loadCore();
		loadCmd();
		loadPerms();

		// CONFIG TESTING!!!!
		/*
		 * config.addCustomCategoryComment("TEST", "this is for testing...");
		 * config.addCustomCategoryComment("TEST.nestedTEST", "MORE TESTING!!!");
		 * config.get("TEST", "test1", false);
		 * config.get("TEST", "test2", false);
		 * config.get("TEST", "test3", false);
		 * config.get("TEST.nestedTEST", "test1", false);
		 * config.get("TEST.nestedTEST", "test2", false);
		 * config.get("TEST.nestedTEST", "test3", false);
		 * config.get("TEST", "testList", new String[] {"lala", "lala", "lala"});
		 * config.get("TEST.nestedTEST", "testList", new String[] {"lala", "lala", "lala"});
		 */

		// Finish init and save.
		config.save();
	}

	private void loadModules()
	{
		config.addCustomCategoryComment("Modules", "Toggles Forge Essentials modules on or off. Set to true to turn on, false to turn off.");

		Property prop = config.get("Modules", "Commands_Enabled", true);
		prop.comment = "Disabling this will remove non-essential commands. ie: /home, /motd, /rules, etc...";
		ModuleLauncher.cmdEnabled = prop.getBoolean(true);

		prop = config.get("Modules", "WorldControl_Enabled", true);
		prop.comment = "Disabling this will remove Selections and selection editing commands such as //set, //copy, etc... Note that this is force disabled if WorldEdit is installed.";
		ModuleLauncher.wcEnabled = prop.getBoolean(true);

		prop = config.get("Modules", "Permissions_Enabled", true);
		prop.comment = "Disabling this will remove any and all permissions integration. Other mods that use this may be affected.";
		ModuleLauncher.permsEnabled = prop.getBoolean(true);
	}

	private void loadCore()
	{
		config.addCustomCategoryComment("Core", "Configure ForgeEssentials Core.");

		Property prop = config.get("Core", "versionCheck", true);
		prop.comment = "Check for newer versions of ForgeEssentials on load?";
		ForgeEssentials.verCheck = prop.getBoolean(true);
		
		prop = config.get("Core", "modlistLocation", "modlist.txt");
		prop.comment = "Specify the file where the modlist will be written to. This path is relative to the ForgeEssentials folder.";
		ForgeEssentials.modlistLocation = prop.value;

	}

	private void loadCmd()
	{
		config.addCustomCategoryComment("Commands", "Configure ForgeEssentials Commands. Only implemented if Commands module is on.");

		Property prop = config.get("Commands", "motd", "Welcome to a server running ForgeEssentials");
		prop.comment = "Specify the message that greets players when they log in to your server.";
		CommandMotd.motd = prop.value;

		prop = config.get("Commands", "RulesFile", "rules.txt");
		prop.comment = "Specify the file where the rules will read from and written to. This path is relative to the ForgeEssentials folder.";
		CommandRules.rulesFile = new File(ForgeEssentials.FEDIR, prop.value);
	}

	private void loadPerms()
	{
		config.addCustomCategoryComment("Permissions", "Configure ForgeEssentials Permissions. Only implemented if Permissions module is enabled.");

		Property prop = config.get("Permissions", "verbose", false);
		prop.comment = "Specify if Verbose mode for Permissions module is enabled. If enabled, every permission registered is printed to the console. Only useful in debugging.";
		ModulePermissions.permsVerbose = prop.getBoolean(false);
	}

	/**
	 * will overwrite the current physical file.
	 */
	public void forceSave()
	{
		config.save();
	}

	/**
	 * @param name
	 * : ie WorldControl, Commands, Permissions, WorldEditCompat, WorldGuardCompat, etc... whatever comes after Module
	 * @return boolean
	 */
	public boolean isModuleEnabled(String name)
	{
		Property prop = config.get("Modules", name + " Enabled", true);
		return prop.getBoolean(true);
	}

	public void changeProperty(String category, String property, String newValue)
	{
		Property prop = config.get(category, property, newValue);
		String oldVal = prop.value;
		prop.value = newValue;

		OutputHandler.logConfigChange(category, property, oldVal, newValue);
	}
}
