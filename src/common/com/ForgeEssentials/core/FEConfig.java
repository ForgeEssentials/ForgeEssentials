package com.ForgeEssentials.core;

import java.io.File;
import java.util.HashMap;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.WorldControl.WorldControl;

public class FEConfig
{

	public static final File FEDIR = new File("./ForgeEssentials/");
	public static final File FECONFIG = new File(FEDIR, "config.cfg");

	private HashMap<String, HashMap<String, Object>> settings = new HashMap<String, HashMap<String, Object>>();

	public WorldControl wc;

	public FEConfig()
	{
		HashMap<String, Object> basicMap = new HashMap<String, Object>();
		basicMap.put("motd", "ForgeEssentials is awesome. https://github.com/ForgeEssentials/ForgeEssentialsMain");
		basicMap.put("rules", "Don't grief or insult ForgeEssentials.");
		settings.put("basic", basicMap);
	}

	public void loadConfig()
	{
		Configuration config = new Configuration(FECONFIG);
		OutputHandler.SOP("Loading config");
		config.load();
		settings.get("basic").put("motd", config.get("basic", "motd", settings.get("basic").get("motd").toString()).value);
		settings.get("basic").put("rules", config.get("basic", "rules", settings.get("basic").get("rules").toString()).value);
		/**
		 * WorldControl
		 */
		
		config.addCustomCategoryComment("WorldControl", "The config area for the WorldControl submod of ForgeEssentials.");

		Property prop;

		prop = config.get("WorldControl", "defaultWandID", (new ItemStack(Item.axeWood)).itemID);
		prop.comment = "The default wand ID. it is set to a wooden axe to start with.";
		wc.defaultWandID = prop.getInt((new ItemStack(Item.axeWood)).itemID);

		prop = config.get("WorldControl", "useExtraSlash", true);
		prop.comment = "Use the extra slash? (eg \"//wand\" instead of \"/wand\")";
		wc.useExtraSlash = prop.getBoolean(true);
		config.save();
	}

	public void changeConfig(String category, String name, Object newValue)
	{
		if (settings.containsKey(category) && settings.get(category).containsKey(name))
			settings.get(category).put(name, newValue);
		FECONFIG.delete();
	}

	public Object getSetting(String category, String name)
	{
		return settings.get(category).get(name);
	}
}
