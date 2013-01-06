package com.ForgeEssentials.core;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.util.MiscEventHandler;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;

public class CoreConfig
{
	public static final File mainconfig = new File(ForgeEssentials.FEDIR, "main.cfg");

	public final Configuration config;

	// this is designed so it will work for any class.
	public CoreConfig()
	{
		OutputHandler.debug("Loading configs");

		config = new Configuration(mainconfig, true);

		config.addCustomCategoryComment("Core", "Configure ForgeEssentials Core.");

		Property prop = config.get("Core", "versionCheck", true);
		prop.comment = "Check for newer versions of ForgeEssentials on load?";
		ForgeEssentials.verCheck = prop.getBoolean(true);

		prop = config.get("Core", "modlistLocation", "modlist.txt");
		prop.comment = "Specify the file where the modlist will be written to. This path is relative to the ForgeEssentials folder.";
		ForgeEssentials.modlistLocation = prop.value;

		prop = config.get("Core", "verbose", false);
		prop.comment = "Specify if Verbose mode is enabled. Only useful in debugging.";
		OutputHandler.verbose = prop.getBoolean(false);

		prop = config.get("Core.Misc", "tpWarmup", 5);
		prop.comment = "The amount of time you need to keep still to tp.";
		TeleportCenter.tpWarmup = prop.getInt(5);

		prop = config.get("Core.Misc", "tpCooldown", 5);
		prop.comment = "The amount of time you need to wait to TP again.";
		TeleportCenter.tpCooldown = prop.getInt(5);

		prop = config.get("Core.Misc", "MajoritySleep", true);
		prop.comment = "If +50% op players sleep, make day.";
		MiscEventHandler.MajoritySleep = prop.getBoolean(true);
		
		config.save();
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
	 *            : ie WorldControl, Commands, Permissions, WorldEditCompat, WorldGuardCompat, etc... whatever comes after Module
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
