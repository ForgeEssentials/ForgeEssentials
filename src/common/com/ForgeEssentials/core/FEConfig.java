package com.ForgeEssentials.core;

import java.io.File;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.WorldControl.WorldControl;

public class FEConfig
{

	public static final File FEDIR = new File("./ForgeEssentials/");
	public static final File FECONFIG = new File(FEDIR, "config.cfg");

	private static String motd = "ForgeEssentials is awesome. https://github.com/ForgeEssentials/ForgeEssentialsMain";

	public static WorldControl wc;

	public static void loadConfig()
	{
		Configuration config = new Configuration(FECONFIG);
		OutputHandler.SOP("Loading config");
		config.load();
		motd = config.get("Basic", "motd", motd).value;
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

	public static void changeConfig(String category, String name, Object newValue)
	{
		if ((category.toLowerCase() + name.toLowerCase()).equals("basicmotd"))
			motd = newValue.toString();
		FECONFIG.delete();
		loadConfig();
	}

	public static String getMotd()
	{
		return motd;
	}
}
