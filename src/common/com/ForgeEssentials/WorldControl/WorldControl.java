package com.ForgeEssentials.WorldControl;

import java.io.File;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.ForgeEssentials;
import com.ForgeEssentials.WorldControl.commands.*;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

// central class for all the WorldControl stuff
public class WorldControl
{
	// implicit constructor WorldControl()
	public static int defaultWandID;
	public static boolean useExtraSlash;
	
	// load.
	public void preLoad(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(ForgeEssentials.FECONFIG);
		config.load();
		
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
	
	// load.
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new WandController());
	}
	
	// load.
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandWand());
	}
}
