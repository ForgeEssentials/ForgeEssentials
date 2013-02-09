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
		OutputHandler.finer("Loading configs");

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
		
		config.addCustomCategoryComment("Core.VIP", "Permission \n" + PlayerTracker.PERMISSION);
		
		prop = config.get("Core.VIP", "KickForVIP", true);
		prop.comment = "Kick a player if not VIP and (playercount + VIP slots = total slots)";
		PlayerTracker.KickForVIP = prop.getBoolean(true);
		
		prop = config.get("Core.VIP", "VIPslots", 5);
		prop.comment = "Amount of space reserved for VIPs";
		PlayerTracker.VIPslots = prop.getInt();
		
		prop = config.get("Core.VIP", "kickMessage", "Sorry, this spot is for VIPs");
		prop.comment = "Message you get when you log in and no VIP space is available";
		PlayerTracker.kickMessage = prop.value;
		
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
}
