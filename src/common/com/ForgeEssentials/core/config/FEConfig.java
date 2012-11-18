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

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.core.OutputHandler;

import com.ForgeEssentials.WorldControl.ModuleWorldControl;

public class FEConfig
{
	public static final File	FECONFIG	= new File(ForgeEssentials.FEDIR, "config.cfg");

	// rules stuff
	public static File			rulesFile	= new File(ForgeEssentials.FEDIR, "rules.txt");
	public ArrayList<String>	rules;

	public final Configuration	config;

	// this is designed so it will work for any class.
	public FEConfig()
	{
		OutputHandler.SOP("Loading configs");
		config = new Configuration(FECONFIG);

		// starting permissions and stuff...

		config.load();

		// Rules  the rules file will be a flat strings file.. nothing special.
		rules = new ArrayList<String>();
		try
		{
			OutputHandler.SOP("Loading rules");
			if (!rulesFile.exists())
			{
				OutputHandler.SOP("No rules file found. Generating with default rules..");
				
				rulesFile.createNewFile();

				// create streams
				FileOutputStream stream = new FileOutputStream(rulesFile);
				OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
				BufferedWriter writer = new BufferedWriter(streamWriter);

				writer.write("# "+rulesFile.getName()+" | numbers are automatically added");
				writer.newLine();
				
				writer.write("Obey the Admins");
				rules.add("Obey the Admins");
				writer.newLine();
				
				writer.write("Do not greif");
				rules.add("Do not greif");
				writer.newLine();
				
				writer.close();
				streamWriter.close();
				stream.close();
				
				OutputHandler.SOP("Completed generating rules file.");
			}
			else
			{
				OutputHandler.SOP("Rules file found. Reading...");
				
				FileInputStream stream = new FileInputStream(rulesFile);
				InputStreamReader streamReader = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(streamReader);

				String read = reader.readLine();
				int counter = 0;
				
				while (read != null)
				{
					// ignore the comment things...
					if (read.startsWith("#"))
						continue;
					
					// add to the rules list.
					rules.add(read);
					
					// read the next string
					read = reader.readLine();
					
					// increment counter
					counter++;
				}

				reader.close();
				streamReader.close();
				stream.close();
				
				OutputHandler.SOP("Completed reading rules file. "+counter+" rules read.");
			}

		}
		catch (Exception e)
		{
			Logger lof = OutputHandler.felog;
			lof.logp(Level.SEVERE, "FEConfig", "Constructor-Rules", "Error reading or writing the Rules file", e);
		}
		
		// other stuff.
	}

	/**
	 * @param name: ei WorldControl, Commands, Permissions, WorldEditCompat, WorldGuardCompat, etc... whatever comes after Module
	 * @return boolean
	 */
	public boolean isModuleEnabled(String name)
	{
		Property prop = config.get("Modules", "name" + "_Enabled", true);
		return prop.getBoolean(true);
	}

	/*
	 * public static final File FECONFIG = new File(ForgeEssentials.FEDIR, "config.cfg");
	 * private HashMap<String, HashMap<String, Object>> settings = new HashMap<String, HashMap<String, Object>>();
	 * public ModuleWorldControl wc;
	 * public ForgeEssentials core;
	 * public ModuleLauncher mdlaunch;
	 * public FEConfig()
	 * {
	 * HashMap<String, Object> basicMap = new HashMap<String, Object>();
	 * basicMap.put("motd", "ForgeEssentials is awesome. https://github.com/ForgeEssentials/ForgeEssentialsMain");
	 * ArrayList<String> rules = new ArrayList<String>();
	 * rules.add("Don't grief");
	 * rules.add("Please grief");
	 * basicMap.put("rules", rules);
	 * settings.put("basic", basicMap);
	 * }
	 * public void loadConfig()
	 * {
	 * Configuration config = new Configuration(FECONFIG);
	 * OutputHandler.SOP("Loading config");
	 * config.load();
	 * settings.get("basic").put("motd", config.get("basic", "motd", settings.get("basic").get("motd").toString()).value);
	 * settings.get("basic").put("rules", config.get("basic", "rules", ((String)settings.get("basic").get("rules"))));
	 * Property prop;
	 * // Version toggling
	 * config.addCustomCategoryComment("modules", "Turn ForgeEssentials modules on or off here.");
	 * prop = config.get("modules", "enableWorldControl", true);
	 * prop.comment = "Enable/disable the WorldControl module. Disabling this on client also disables the CUI.";
	 * mdlaunch.wcenabled = prop.getBoolean(true);
	 * prop = config.get("basic", "checkForUpdates", true);
	 * prop.comment = "Enables/disables the Commands module.";
	 * mdlaunch.cmdenabled = prop.getBoolean(true);
	 * // Core
	 * prop = config.get("basic", "checkForUpdates", true);
	 * prop.comment = "Check for updates to ForgeEssentials on load. If you turn this off, you can still use /feversion in game.";
	 * core.verCheck = prop.getBoolean(true);
	 * // WorldControl (depreciated)
	 * config.addCustomCategoryComment("WorldControl", "The config area for the WorldControl submod of ForgeEssentials.");
	 * prop = config.get("WorldControl", "defaultWandID", (new ItemStack(Item.axeWood)).itemID);
	 * prop.comment = "The default wand ID. it is set to a wooden axe to start with.";
	 * wc.defaultWandID = prop.getInt((new ItemStack(Item.axeWood)).itemID);
	 * prop = config.get("WorldControl", "useExtraSlash", true);
	 * prop.comment = "Use the extra slash? (eg \"//wand\" instead of \"/wand\")";
	 * wc.useExtraSlash = prop.getBoolean(true);
	 * config.save();
	 * }
	 * public void changeConfig(String category, String name, Object newValue)
	 * {
	 * if (settings.containsKey(category) && settings.get(category).containsKey(name))
	 * settings.get(category).put(name, newValue);
	 * FECONFIG.delete();
	 * }
	 * public Object getSetting(String category, String name)
	 * {
	 * return settings.get(category).get(name);
	 * }
	 */
}
