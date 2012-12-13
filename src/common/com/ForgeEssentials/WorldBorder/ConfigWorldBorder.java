package com.ForgeEssentials.WorldBorder;

import java.io.File;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.WorldBorder.Penalties.IPenalty;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigWorldBorder 
{
	public static final File wbconfig = new File(ForgeEssentials.FEDIR, "WorldBorder.cfg");
	public static final File wbconfigExample = new File(ForgeEssentials.FEDIR, "WorldBorder_Example.cfg");
	public final Configuration config;
	public static Configuration configExample;
	
	public ConfigWorldBorder()
	{
		if(wbconfigExample.exists())
			wbconfigExample.delete();
		makeExamples();
		
		int maxReach = 0;
		config = new Configuration(wbconfig, true);
		String penaltyBasePackage = IPenalty.class.getPackage().getName() + ".";
		
		config.addCustomCategoryComment("Penalties", "This is what will happen to the player if he passes the world boder.");
		
		String[] stages = {"Example"};
		stages = config.get("Penalties", "stages", stages, "If you add an item here, a subcategory will be generated").valueList;
		
		for(String stage : stages)
		{
			String cat = "Penalties." + stage;
			
			int dist = config.get(cat, "Distance", 0, "The distance outside the border when this gets activated. WARING this needs to be unique! You can specify 2 penalties in 1 stage.").getInt();
			if(dist > maxReach) maxReach = dist;
			
			String[] kinds = {"message"};
			kinds = config.get(cat, "kind", kinds, "Get the list of possibilitys on the github wiki.").valueList;
			
			for(String kind : kinds)
			{
				try
				{
					Class c = Class.forName(penaltyBasePackage + kind.toLowerCase());
					try 
					{
						IPenalty pentalty = (IPenalty) c.newInstance();
						pentalty.registerConfig(config, cat + "." + kind);
						ModuleWorldBorder.registerPenalty(dist, pentalty);
					}
					catch (Exception e) 
					{
						OutputHandler.SOP("Could not initialize '" + kind + "' in stage '" + stage + "'");
					}
				}
				catch (ClassNotFoundException e)
				{
					OutputHandler.SOP("'" + kind + "' in the stage '" + stage + "' does not exist!");
				}
			}
		}
		
		ModuleWorldBorder.maxReach = maxReach;
		config.save();
	}
	
	public static void makeExamples()
	{
		configExample = new Configuration(wbconfigExample, true);
		String penaltyBasePackage = IPenalty.class.getPackage().getName() + ".";
		
		configExample.addCustomCategoryComment("Penalties", "This is what will happen to the player if he passes the world boder.");
		
		String[] stages = {"Stage1"};
		stages = configExample.get("Penalties", "stages", stages, "If you add an item here, a subcategory will be generated").valueList;
		
		makeExamplesStage1();
		makeExamplesStage2();
		
		configExample.save();
	}
	
	public static void makeExamplesStage1()
	{
		String penaltyBasePackage = IPenalty.class.getPackage().getName() + ".";
		String stage = "Stage1";
		String cat = "Penalties." + stage;
		
		int dist = configExample.get(cat, "Distance", 0, "The distance outside the border when this gets activated. WARING this needs to be unique! You can specify 2 penalties in 1 stage.").getInt();
		
		String[] kinds = {"message"};
		kinds = configExample.get(cat, "kind", kinds, "Get the list of possibilitys on the github wiki.").valueList;
		
		configExample.get(cat + "." + "message", "Message", "YOU SHALL NOT PASS!", "Message to send to the player. You can use color codes.");
	}
	
	public static void makeExamplesStage2()
	{
		String penaltyBasePackage = IPenalty.class.getPackage().getName() + ".";
		String stage = "Stage2";
		String cat = "Penalties." + stage;
		
		int dist = configExample.get(cat, "Distance", 10, "The distance outside the border when this gets activated. WARING this needs to be unique! You can specify 2 penalties in 1 stage.").getInt();
		
		String[] kinds = {"message", "knockback"};
		kinds = configExample.get(cat, "kind", kinds, "Get the list of possibilitys on the github wiki.").valueList;
		
		configExample.get(cat + "." + "message", "Message", "Told ya!", "Message to send to the player. You can use color codes.");
		
		configExample.get(cat + "." + "knockback", "Strenght_XZ", 1, "The knockback strenght in the horizontal plane.").getInt();
		configExample.get(cat + "." + "knockback", "Strenght_Y", 1, "The knockback strenght in the vertical plane. Pos numers are up, neg are down.").getInt();
	
	}

}
