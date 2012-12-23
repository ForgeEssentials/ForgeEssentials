package com.ForgeEssentials.WorldBorder;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.WorldBorder.Effects.IEffect;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

/**
 * This generates the configuration structure + an example file.
 * 
 * @author Dries007
 *
 */

public class ConfigWorldBorder 
{
	public static final File wbconfig = new File(ForgeEssentials.FEDIR, "WorldBorder.cfg");
	public static final File wbconfigExample = new File(ForgeEssentials.FEDIR, "WorldBorder_Example.cfg");
	public final Configuration config;
	public static Configuration configExample;
	
	/**
	 * This list makes sure the effect is in the example file.
	 * Not used for parsing the list from the config.
	 */
	public static final List<String> PossibleEffects = Arrays.asList("knockback", "message", "potion", "damage", "smite", "serverkick", "executecommand");
	
	public ConfigWorldBorder()
	{
		if(wbconfigExample.exists())
			wbconfigExample.delete();
		makeExamples();
		
		config = new Configuration(wbconfig, true);
		
		penaltiesConfig(config);
		commonConfig(config);
		
		config.save();
	}
	
	/**
	 * Does all the rest of the config
	 * @param config
	 */
	public static void commonConfig(Configuration config)
	{
		String category = "Settings";
		config.addCustomCategoryComment(category, "Common settings.");
		
		ModuleWorldBorder.logToConsole = config.get(category, "LogToConsole", true, "Enable logging to the server console & the log file").getBoolean(true);
		Property prop = config.get(category, "overGenerate", 345);
			prop.comment = "The amount of blocks that will be generated outside the radius of the border. This is important!" +
					" \nIf you set this high, you will need exponentially more time while generating, but you won't get extra land if a player does pass the border." +
					" \nIf you use something like Dynmap you should put this number higher, if the border is not there for aesthetic purposes, then you don't need that." +
					" \nThe default value (345) is calcultated like this: (20 chuncks for vieuw distance * 16 blocks per chunck) + 25 as backup" +
					" \nThis allows players to pass the border 25 blocks before generating new land.";
		ModuleWorldBorder.overGenerate = prop.getInt(345);
	}
	
	/**
	 * Does penalty part on config
	 * @param config
	 */
	public static void penaltiesConfig(Configuration config)
	{
		String penaltyBasePackage = IEffect.class.getPackage().getName() + ".";
		config.addCustomCategoryComment("Penalties", "This is what will happen to the player if he passes the world boder.");
		
		String[] stages = {"Stage1"};
		stages = config.get("Penalties", "stages", stages, "If you add an item here, a subcategory will be generated").valueList;
		
		for(String stage : stages)
		{
			String cat = "Penalties." + stage;
			
			int dist = config.get(cat, "Distance", 0, "The distance outside the border when this gets activated. WARING this needs to be unique! You can specify 2 penalties in 1 stage.").getInt();
			
			String[] effects = {"message", "knockback", "damage"};
			effects = config.get(cat, "effects", effects, "Get the list of possibilitys in the example file").valueList;
			
			IEffect[] effctList = new IEffect[effects.length];
			int i = 0;
			for(String effect : effects)
			{
				try
				{
					Class c = Class.forName(penaltyBasePackage + effect.toLowerCase());
					try 
					{
						IEffect pentalty = (IEffect) c.newInstance();
						pentalty.registerConfig(config, cat + "." + effect);
						effctList[i] = pentalty;
						i++;
					}
					catch (Exception e) 
					{
						OutputHandler.SOP("Could not initialize '" + effect + "' in stage '" + stage + "'");
					}
				}
				catch (ClassNotFoundException e)
				{
					OutputHandler.SOP("'" + effect + "' in the stage '" + stage + "' does not exist!");
				}
			}
			
			ModuleWorldBorder.registerEffects(dist, effctList);
		}
	}
	
	/*
	 * Example File
	 */
	
	public static void makeExamples()
	{
		configExample = new Configuration(wbconfigExample, true);
		String penaltyBasePackage = IEffect.class.getPackage().getName() + ".";
		
		/*
		 * PENALTIES
		 */
		
		configExample.addCustomCategoryComment("Penalties", "This is just an example file!");
		String[] stages = {"Stage1", "Stage2"};
		stages = configExample.get("Penalties", "stages", stages, "If you add an item here, a subcategory will be generated").valueList;
		
		makeExamplesStage1();
		makeExamplesStage2();
		
		configExample.addCustomCategoryComment("Effects", "List of all possible effects you can have in a stage.");
		
		for(String effect : PossibleEffects)
		{
			try
			{
				Class c = Class.forName(penaltyBasePackage + effect.toLowerCase());
				try 
				{
					IEffect pentalty = (IEffect) c.newInstance();
					pentalty.registerConfig(configExample, "Effects." + effect);
				}
				catch (Exception e) 
				{
					OutputHandler.SOP("Could not initialize '" + effect + "' in stage '" + "EXAMPLE_FILE" + "'");
				}
			}
			catch (ClassNotFoundException e)
			{
				OutputHandler.SOP("'" + effect + "' in the stage '" + "EXAMPLE_FILE" + "' does not exist!");
			}
		}
		
		/*
		 * COMMON SETTINGS
		 */
		
		commonConfig(configExample);
		
		
		configExample.save();
	}
	
	public static void makeExamplesStage1()
	{
		String penaltyBasePackage = IEffect.class.getPackage().getName() + ".";
		String stage = "Stage1";
		String cat = "Penalties." + stage;
		
		int dist = configExample.get(cat, "Distance", 0, "The distance outside the border when this gets activated. WARING this needs to be unique! You can specify 2 penalties in 1 stage.").getInt();
		
		String[] kinds = {"message", "potion"};
		kinds = configExample.get(cat, "kind", kinds, "Get the list of possibilitys on the github wiki.").valueList;
		
		String[] potionEffects = {"9:5:0"};
		configExample.get(cat + "." + "potion", "potionEffects", potionEffects, "Format like this: 'ID:duration:amplifire'");
		configExample.get(cat + "." + "message", "Message", "YOU SHALL NOT PASS!", "Message to send to the player. You can use color codes.");
	}
	
	public static void makeExamplesStage2()
	{
		String penaltyBasePackage = IEffect.class.getPackage().getName() + ".";
		String stage = "Stage2";
		String cat = "Penalties." + stage;
		
		int dist = configExample.get(cat, "Distance", 10, "The distance outside the border when this gets activated. WARING this needs to be unique! You can specify 2 penalties in 1 stage.").getInt();
		
		String[] kinds = {"message", "knockback", "damage"};
		kinds = configExample.get(cat, "kind", kinds, "Get the list of possibilitys on the github wiki.").valueList;
		
		configExample.addCustomCategoryComment(cat + ".knockback", "This effect has no option.");
		configExample.get(cat + "." + "message", "Message", "Told ya!", "Message to send to the player. You can use color codes.");
		configExample.get(cat + "." + "damage", "damage", 2, "Amount of damage in 1/2 harts.");
	}

}
