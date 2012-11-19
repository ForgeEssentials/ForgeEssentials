package com.ForgeEssentials.core.config;

import java.io.File;

import com.ForgeEssentials.commands.CommandMotd;
import com.ForgeEssentials.commands.CommandRules;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.core.OutputHandler;

public class FEConfig
{
	public static final File FECONFIG = new File(ForgeEssentials.FEDIR, "config.cfg");

	public final Configuration config;

	// this is designed so it will work for any class.
	public FEConfig()
	{
		OutputHandler.SOP("Loading configs");
		config = new Configuration(FECONFIG, true);
		// config.load -- COnfigurations are loaded on Construction.

		// load the modules
		loadModules();

		// miscellanious stuff...
		loadMisc();
		
		// CONFIG TESTING!!!!
		config.addCustomCategoryComment("TEST", "this is for testing...");
		config.addCustomCategoryComment("TEST.nestedTEST", "MORE TESTING!!!");
		
		config.get("TEST", "test1", false);
		config.get("TEST", "test2", false);
		config.get("TEST", "test3", false);
		config.get("TEST.nestedTEST", "test1", false);
		config.get("TEST.nestedTEST", "test2", false);
		config.get("TEST.nestedTEST", "test3", false);
		
		config.get("TEST", "testList", new String[] {"lala", "lala", "lala"});
		config.get("TEST.nestedTEST", "testList", new String[] {"lala", "lala", "lala"});

		// Finish init and save.
		config.save();
	}

	private void loadModules()
	{
		config.addCustomCategoryComment("Modules", "Here you can Enable and Disable ForgeEssentials Modules");

		Property prop = config.get("Modules", "Commands_Enabled", true);
		prop.comment = "Disabling this will remove non-essential commands. ie: /home, /motd, /rules, etc...";
		ModuleLauncher.cmdEnabled = prop.getBoolean(true);

		prop = config.get("Modules", "WorldControl_Enabled", true);
		prop.comment = "Disabling this will remove Selections and selection editing commands such as //set, //copy, etc...";
		ModuleLauncher.wcEnabled = prop.getBoolean(true);

		prop = config.get("Modules", "Permissions_Enabled", true);
		prop.comment = "Disabling this will remove any and all permissions integration. Other mods that use this may be affected.";
		ModuleLauncher.permsEnabled = prop.getBoolean(true);
	}

	private void loadMisc()
	{
		config.addCustomCategoryComment("Miscellaneous", "here you can configure miscellanious things.");

		Property prop = config.get("Miscellaneous", "motd", "Welcome to a server running ForgeEssentials");
		prop.comment = "Specify the message that greets players when they log in to your server. Only ";
		CommandMotd.motd = prop.value;

		prop = config.get("Miscellaneous", "versionCheck", true);
		prop.comment = "Check for newer versions of ForgeEssentials on load?";
		ForgeEssentials.verCheck = prop.getBoolean(true);

		prop = config.get("Miscellaneous", "RulesFile", "rules.txt");
		prop.comment = "Specify the file where the rules will read from and written to. This path is relative to the ForgeEssentials folder.";
		CommandRules.rulesFile = new File(ForgeEssentials.FEDIR, prop.value);
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
	 *            : ei WorldControl, Commands, Permissions, WorldEditCompat, WorldGuardCompat, etc... whatever comes after Module
	 * @return boolean
	 */
	public boolean isModuleEnabled(String name)
	{
		Property prop = config.get("Modules", name + " Enabled", true);
		return prop.getBoolean(true);
	}

	public void changeMiscProperty(String property, String newValue)
	{
		Property prop = config.get("Miscellaneous", property, newValue);
		String oldVal = prop.value;
		prop.value = newValue;

		OutputHandler.logConfigChange("Miscellaneous", property, oldVal, newValue);
	}

	public void changeProperty(String category, String property, String newValue)
	{
		Property prop = config.get(category, property, newValue);
		String oldVal = prop.value;
		prop.value = newValue;

		OutputHandler.logConfigChange(category, property, oldVal, newValue);
	}
}
