package com.ForgeEssentials.WorldBorder;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder.BorderShape;
import com.ForgeEssentials.WorldBorder.Effects.IEffect;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IModuleConfig;
import com.ForgeEssentials.util.OutputHandler;

/**
 * This generates the configuration structure
 * 
 * @author Dries007
 * 
 */

public class ConfigWorldBorder implements IModuleConfig
{
	public static final File wbconfig = new File(ForgeEssentials.FEDIR, "WorldBorder.cfg");

	private Configuration config;

	public ConfigWorldBorder()
	{

	}

	/**
	 * Does penalty part on config
	 * 
	 * @param config
	 */
	public static void penaltiesConfig(Configuration config)
	{
		ModuleWorldBorder.effectsList.clear();

		String penaltyBasePackage = "com.ForgeEssentials.WorldBorder.Effects.";
		config.addCustomCategoryComment("Penalties", "This is what will happen to the player if he passes the world boder.");

		String[] stages = { "Stage1" };
		stages = config.get("Penalties", "stages", stages, "If you add an item here, a subcategory will be generated").valueList;

		for (String stage : stages)
		{
			String cat = "Penalties." + stage;

			int dist = config.get(cat, "Distance", 0,
					"The distance outside the border when this gets activated. WARING this needs to be unique! You can specify 2 penalties in 1 stage.")
					.getInt();

			String[] effects = { "message", "knockback", "damage" };
			effects = config.get(cat, "effects", effects, "Get the list of possibilitys in the example file").valueList;

			IEffect[] effctList = new IEffect[effects.length];
			int i = 0;
			for (String effect : effects)
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

	/**
	 * Does all the rest of the config
	 * 
	 * @param config
	 */
	public static void commonConfig(Configuration config)
	{
		String category = "Settings";
		config.addCustomCategoryComment(category, "Common settings.");

		ModuleWorldBorder.logToConsole = config.get(category, "LogToConsole", true, "Enable logging to the server console & the log file").getBoolean(true);
		Property prop = config.get(category, "overGenerate", 345);
		prop.comment = "The amount of blocks that will be generated outside the radius of the border. This is important!"
				+ " \nIf you set this high, you will need exponentially more time while generating, but you won't get extra land if a player does pass the border."
				+ " \nIf you use something like Dynmap you should put this number higher, if the border is not there for aesthetic purposes, then you don't need that."
				+ " \nThe default value (345) is calcultated like this: (20 chuncks for vieuw distance * 16 blocks per chunck) + 25 as backup"
				+ " \nThis allows players to pass the border 25 blocks before generating new land.";
		ModuleWorldBorder.overGenerate = prop.getInt(345);

		category = "Point";
		config.addCustomCategoryComment(category, "Location. In all worlds the same!");

		int X = config.get(category, "X", 0).getInt();
		int Z = config.get(category, "Z", 0).getInt();
		int rad = config.get(category, "rad", 0).getInt();
		BorderShape shape = BorderShape.valueOf(config.get(category, "shape", "square").value);
		boolean set = config.get(category, "set", false, "True if the value is actually set.").getBoolean(false);

		ModuleWorldBorder.setCenter(rad, X, Z, shape, set);
	}

	@Override
	public void init()
	{
		config = new Configuration(wbconfig, true);
		penaltiesConfig(config);
		commonConfig(config);
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config = new Configuration(wbconfig, true);
		penaltiesConfig(config);
		commonConfig(config);
		config.save();
	}

	@Override
	public File getFile()
	{
		return wbconfig;
	}

	@Override
	public void forceSave()
	{
		String category = "Point";
		config.get(category, "X", 0).value = "" + ModuleWorldBorder.X;
		config.get(category, "Z", 0).value = "" + ModuleWorldBorder.Z;
		config.get(category, "rad", 0).value = "" + ModuleWorldBorder.rad;
		config.get(category, "set", false, "True if the value is actually set.").value = "" + ModuleWorldBorder.set;
		config.get(category, "shape", "square").value = ModuleWorldBorder.shape.toString();
	}

	@Override
	public void setGenerate(boolean generate)
	{
	}

}
