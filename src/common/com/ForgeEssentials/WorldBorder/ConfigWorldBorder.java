package com.ForgeEssentials.WorldBorder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.WorldBorder.Effects.*;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.DataDriver;
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
	public static final List<String> PossibleEffects = Arrays.asList("knockback", "message");
	
	public ConfigWorldBorder()
	{
		if(wbconfigExample.exists())
			wbconfigExample.delete();
		makeExamples();
		
		config = new Configuration(wbconfig, true);
		String penaltyBasePackage = IEffect.class.getPackage().getName() + ".";
		
		config.addCustomCategoryComment("Penalties", "This is what will happen to the player if he passes the world boder.");
		
		String[] stages = {"Stage1"};
		stages = config.get("Penalties", "stages", stages, "If you add an item here, a subcategory will be generated").valueList;
		
		for(String stage : stages)
		{
			String cat = "Penalties." + stage;
			
			int dist = config.get(cat, "Distance", 0, "The distance outside the border when this gets activated. WARING this needs to be unique! You can specify 2 penalties in 1 stage.").getInt();
			
			String[] effects = {"message", "knockback"};
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
			
			ModuleWorldBorder.registerEffect(dist, effctList);
		}
		config.save();
	}
	
	/*
	 * Example File
	 */
	
	public static void makeExamples()
	{
		configExample = new Configuration(wbconfigExample, true);
		String penaltyBasePackage = IEffect.class.getPackage().getName() + ".";
		
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
		
		configExample.save();
	}
	
	public static void makeExamplesStage1()
	{
		String penaltyBasePackage = IEffect.class.getPackage().getName() + ".";
		String stage = "Stage1";
		String cat = "Penalties." + stage;
		
		int dist = configExample.get(cat, "Distance", 0, "The distance outside the border when this gets activated. WARING this needs to be unique! You can specify 2 penalties in 1 stage.").getInt();
		
		String[] kinds = {"message"};
		kinds = configExample.get(cat, "kind", kinds, "Get the list of possibilitys on the github wiki.").valueList;
		
		configExample.get(cat + "." + "message", "Message", "YOU SHALL NOT PASS!", "Message to send to the player. You can use color codes.");
	}
	
	public static void makeExamplesStage2()
	{
		String penaltyBasePackage = IEffect.class.getPackage().getName() + ".";
		String stage = "Stage2";
		String cat = "Penalties." + stage;
		
		int dist = configExample.get(cat, "Distance", 10, "The distance outside the border when this gets activated. WARING this needs to be unique! You can specify 2 penalties in 1 stage.").getInt();
		
		String[] kinds = {"message", "knockback"};
		kinds = configExample.get(cat, "kind", kinds, "Get the list of possibilitys on the github wiki.").valueList;
		
		configExample.addCustomCategoryComment(cat + ".knockback", "This effect has no option.");
		configExample.get(cat + "." + "message", "Message", "Told ya!", "Message to send to the player. You can use color codes.");
	}

}
