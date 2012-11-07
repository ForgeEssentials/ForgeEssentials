package com.ForgeEssentials.core;

import java.io.File;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class FEConfig
{

	public static final File FEDIR = new File("./config/ForgeEssentials/");
	public static final File FECONFIG = new File(FEDIR, "config.cfg");
	public static final File FEMODULES = new File(FEDIR, "modules.cfg");
	//Permissions
	public static final File PERMSSTORE = new File(FEDIR, "/perms");
	public static FEConfig instance;
	
	// Default values - MUST BE CATEGORY THEN NAME, ALL LOWER CASE
	
	//WorldControl
	public static int defaultWandID;
	public static boolean useExtraSlash;
	//Commands
	public static String motd = "ForgeEssentials is awesome. https://github.com/ForgeEssentials/ForgeEssentialsMain";

	public static void loadConfig()
	{
		
		Configuration config = new Configuration(FECONFIG);
		OutputHandler.SOP("Loading config");
		config.load();
		//Commands
		motd = config.get("Basic", "motd", motd).value;
		//WorldControl
		config.addCustomCategoryComment("WorldControl", "The config area for the WorldControl submod of ForgeEssentials.");
		
		Property prop;
		
		prop = config.get("WorldControl", "defaultWandID", (new ItemStack(Item.axeWood)).itemID);
		prop.comment = "The default wand ID. it is set to a wooden axe to start with.";
		defaultWandID = prop.getInt((new ItemStack(Item.axeWood)).itemID);
		
		prop = config.get("WorldControl", "useExtraSlash", true);
		prop.comment = "Use the extra slash? (eg \"//wand\" instead of \"/wand\")";
		useExtraSlash = prop.getBoolean(true);
		config.save();
	}

	public void changeConfig(String category, String name, Object newValue)
	{
		try
		{
			getClass().getField(category.toLowerCase() + name.toLowerCase()).set(this, newValue);
		} catch (Exception e)
		{
			OutputHandler.SOP("Could not change config setting, a dev probably messed something up.");
		}
		FECONFIG.delete();
		loadConfig();
	}
	
}
