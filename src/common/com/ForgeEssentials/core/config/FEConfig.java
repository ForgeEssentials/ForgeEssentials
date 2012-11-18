package com.ForgeEssentials.core.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.core.OutputHandler;

import com.ForgeEssentials.WorldControl.ModuleWorldControl;

public class FEConfig
{
	public static final File	FECONFIG	= new File(ForgeEssentials.FEDIR, "config.cfg");

	// rules stuff
	public ArrayList<String>	rules;

	public final Configuration	config;

	// this is designed so it will work for any class.
	public FEConfig()
	{
		OutputHandler.SOP("Loading configs");
		config = new Configuration(FECONFIG);
		// config.load  -- COnfigurations are loaded on Construction.
		
		// load the modules
		loadModules();
		
		// miscellanious stuff...
		loadMisc();
		
		// Finish init and save.
		config.save();
	}
	
	private void loadModules()
	{
		config.addCustomCategoryComment("Modules", "Here you can Enable and Disable ForgeEssentials Modules");
		
		Property prop = config.get("Modules", "Commands_Enabled", true);
		prop.comment = "Disabling this will remove the non essentials commands. ie: /home, /motd, /rules, etc...";
		ModuleLauncher.cmdEnabled = prop.getBoolean(true);
		
		prop = config.get("Modules", "WorldControl_Enabled", true);
		prop.comment = "Disabling this will remove Selections and selection editting commands such as //set, //copy, etc...";
		ModuleLauncher.wcEnabled = prop.getBoolean(true);
		
		prop = config.get("Modules", "Permissions_Enabled", true);
		prop.comment = "Disabling this will remove any and all permissions integration";
		ModuleLauncher.permsEnabled = prop.getBoolean(true);
	}
	
	private void loadMisc()
	{
		config.addCustomCategoryComment("Miscellaneous", "here you can configure miscellanious things.");
		
		Property prop = config.get("Miscellaneous", "motd", "Welcome to a server running ForgeEssentials");
		prop.comment = "the Message Of The Day is only used if the Commands module is enabled.";
		ModuleCommands.motd = prop.value;
		
		prop = config.get("Miscellaneous", "versionCheck", true);
		prop.comment = "to check for newer versions of ForgeEssentials or not.";
		ForgeEssentials.verCheck = prop.getBoolean(true);
		
		prop = config.get("Miscellaneous", "RulesFile", "rules.txt");
		prop.comment = "the file where the rules will read from and written to. This path is relative to the ForgeEssentials folder.";
		ModuleCommands.rulesFile = new File(ForgeEssentials.FEDIR, prop.value);
	}
	
	/**
	 * will overwrite the current physical file.
	 */
	public void forceSave()
	{
		config.save();
	}

	/**
	 * @param name: ei WorldControl, Commands, Permissions, WorldEditCompat, WorldGuardCompat, etc... whatever comes after Module
	 * @return boolean
	 */
	public boolean isModuleEnabled(String name)
	{
		Property prop = config.get("Modules", name + "_Enabled", true);
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
