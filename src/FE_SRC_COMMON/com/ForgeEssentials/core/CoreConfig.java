package com.ForgeEssentials.core;

import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.core.compat.CompatReiMinimap;
import com.ForgeEssentials.core.compat.DuplicateCommandRemoval;
import com.ForgeEssentials.util.MiscEventHandler;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.io.File;

public class CoreConfig
{
	public static final File	mainconfig	= new File(ForgeEssentials.FEDIR, "main.cfg");

	public final Configuration	config;

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

		DuplicateCommandRemoval.removeDuplicateCommands = config.get("general", "removeDuplicateCommands", true, "Remove commands from the list if they already exist outside of FE.").getBoolean(true);

		prop = config.get("Core", "verbose", false);
		prop.comment = "Specify if Verbose mode is enabled. Only useful in debugging.";
		OutputHandler.verbose = prop.getBoolean(false);

		prop = config.get("Core.Misc", "tpWarmup", 5);
		prop.comment = "The amount of time you need to stand still to TP.";
		TeleportCenter.tpWarmup = prop.getInt(5);

		prop = config.get("Core.Misc", "tpCooldown", 5);
		prop.comment = "The amount of time you need to wait to TP again.";
		TeleportCenter.tpCooldown = prop.getInt(5);

		prop = config.get("Core.Misc", "MajoritySleep", true);
		prop.comment = "If +50% of players sleep, make it day.";
		MiscEventHandler.MajoritySleep = prop.getBoolean(true);

		config.addCustomCategoryComment("Core.ReisMinimap", "Use this to enable certain Rei's Minimap options. They will be added to the server's MOTD automatically.");

		prop = config.get("Core.ReisMinimap", "caveMap", false);
		CompatReiMinimap.cavemap = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarPlayer", false);
		CompatReiMinimap.radarPlayer = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarAnimal", false);
		CompatReiMinimap.radarAnimal = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarMod", false);
		CompatReiMinimap.radarMod = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarSlime", false);
		CompatReiMinimap.radarSlime = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarSquid", false);
		CompatReiMinimap.radarSquid = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarOther", false);
		CompatReiMinimap.radarOther = prop.getBoolean(false);

		config.save();
	}

	/**
	 * will overwrite the current physical file.
	 */
	public void forceSave()
	{
		config.save();
		
		config.get("general", "removeDuplicateCommands", true, "Remove commands from the list if they already exist outside of FE.").value = ""	+ DuplicateCommandRemoval.removeDuplicateCommands;
	}

	/**
	 * @param name : ie WorldControl, Commands, Permissions, WorldEditCompat, WorldGuardCompat, etc... whatever comes after Module
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
