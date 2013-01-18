package com.ForgeEssentials.core;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.util.MiscEventHandler;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;

import cpw.mods.fml.common.FMLCommonHandler;

public class CoreConfig
{
	public static final File mainconfig = new File(ForgeEssentials.FEDIR, "main.cfg");

	public final Configuration config;

	private boolean cavemap;

	private boolean radarPlayer;

	private boolean radarAnimal;

	private boolean radarMod;

	private boolean radarSlime;

	private boolean radarSquid;

	private boolean radarOther;

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
		
		config.addCustomCategoryComment("Core.ReisMinimap", "Use this to enable sertain Rei's Minimap options. They will be added to the server's MOTD automatically.");
		
		prop = config.get("Core.ReisMinimap", "caveMap", false);
		cavemap = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarPlayer", false);
		radarPlayer = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarAnimal", false);
		radarAnimal = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarMod", false);
		radarMod = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarSlime", false);
		radarSlime = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarSquid", false);
		radarSquid = prop.getBoolean(false);
		prop = config.get("Core.ReisMinimap", "radarOther", false);
		radarOther = prop.getBoolean(false);
		
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

	public String reimotd()
	{
		try
		{	
			String MOTD = "\u00a7e\u00a7f";
			
			if(radarOther) MOTD 	= "\u00a77" + MOTD;
			if(radarSquid) MOTD 	= "\u00a76" + MOTD;
			if(radarSlime) MOTD 	= "\u00a75" + MOTD;
			if(radarMod) MOTD 		= "\u00a74" + MOTD;
			if(radarAnimal) MOTD 	= "\u00a73" + MOTD;
			if(radarPlayer) MOTD 	= "\u00a72" + MOTD;
			if(cavemap) MOTD 		= "\u00a71" + MOTD;
			
			MOTD = "\u00a70\u00a70" + MOTD;
			
			OutputHandler.debug("Rei's minimap settings: " + MOTD.replaceAll("\u00a7", "&"));
			
			return MOTD;
		}
		catch (Exception e)
		{e.printStackTrace();}
		return "";
	}
}
