package com.ForgeEssentials.WorldBorder;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder.BorderShape;
import com.ForgeEssentials.WorldBorder.Effects.IEffect;
import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.ForgeEssentials.util.OutputHandler;

/**
 * This generates the configuration structure
 * @author Dries007
 */

public class ConfigWorldBorder extends ModuleConfigBase
{
	private Configuration	config;

	public ConfigWorldBorder(File file)
	{
		super(file);
	}

	/**
	 * Does penalty part on config
	 * @param config
	 */
	public static void penaltiesConfig(Configuration config)
	{
		ModuleWorldBorder.effectsList.clear();

		String penaltyBasePackage = "com.ForgeEssentials.WorldBorder.Effects.";
		config.addCustomCategoryComment("Penalties", "This is what will happen to the player if he passes the world border.");

		String[] stages =
		{ "Stage1" };
		stages = config.get("Penalties", "stages", stages, "If you add an item here, a subcategory will be generated.").getStringList();

		for (String stage : stages)
		{
			String cat = "Penalties." + stage;

			int dist = config.get(cat, "Distance", 0, "The distance outside the border when this gets activated. WARNING: This needs to be unique! You can specify 2 penalties in 1 stage.").getInt();

			String[] effects =
			{ "message", "knockback", "damage" };
			effects = config.get(cat, "effects", effects, "Get the list of possibilities in the example file").getStringList();

			IEffect[] effctList = new IEffect[effects.length];
			int i = 0;
			for (String effect : effects)
			{
				try
				{
					Class<?> c = Class.forName(penaltyBasePackage + effect.toLowerCase());
					try
					{
						IEffect pentalty = (IEffect) c.newInstance();
						pentalty.registerConfig(config, cat + "." + effect);
						effctList[i] = pentalty;
						i++;
					}
					catch (Exception e)
					{
						OutputHandler.info("Could not initialize '" + effect + "' in stage '" + stage + "'");
					}
				}
				catch (ClassNotFoundException e)
				{
					OutputHandler.info("'" + effect + "' in the stage '" + stage + "' does not exist!");
				}
			}

			ModuleWorldBorder.registerEffects(dist, effctList);
		}
	}

	/**
	 * Does all the rest of the config
	 * @param config
	 */
	public static void commonConfig(Configuration config)
	{
		String category = "Settings";
		config.addCustomCategoryComment(category, "Common settings.");

		ModuleWorldBorder.logToConsole = config.get(category, "LogToConsole", true, "Enable logging to the server console and the log file").getBoolean(true);
		TickTaskFill.debug = config.get(category, "debug", false).getBoolean(false);

		Property prop = config.get(category, "overGenerate", 345);
		prop.comment = "The amount of blocks that will be generated outside the radius of the border. This is important!"
				+ " \nIf you set this high, you will need exponentially more time while generating, but you won't get extra land if a player does pass the border."
				+ " \nIf you use something like Dynmap you should put this number higher. If the border isn't there for aesthetic purposes, then you don't need that."
				+ " \nThe default value (345) is calculated like this: (20 chuncks for view distance * 16 blocks per chunck) + 25 as backup." + " \nThis allows players to pass the border 25 blocks before generating new land.";
		ModuleWorldBorder.overGenerate = prop.getInt(345);

		category = "Point";
		config.addCustomCategoryComment(category, "Location. This is the same in all worlds!");

		int X = config.get(category, "X", 0).getInt();
		int Z = config.get(category, "Z", 0).getInt();
		int rad = config.get(category, "rad", 0).getInt();
		BorderShape shape = BorderShape.valueOf(config.get(category, "shape", "square").getString());
		boolean set = config.get(category, "set", false, "True if the value is actually set.").getBoolean(false);

		ModuleWorldBorder.setCenter(rad, X, Z, shape, set);
	}

	@Override
	public void init()
	{
		config = new Configuration(file, true);
		penaltiesConfig(config);
		commonConfig(config);
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config = new Configuration(file, true);
		penaltiesConfig(config);
		commonConfig(config);
		config.save();
	}

	@Override
	public void forceSave()
	{
		String category = "Point";
		config.get(category, "X", 0).set(ModuleWorldBorder.X);
		config.get(category, "Z", 0).set(ModuleWorldBorder.Z);
		config.get(category, "rad", 0).set(ModuleWorldBorder.rad);
		config.get(category, "set", false, "True if the value is actually set.").set(ModuleWorldBorder.set);
		config.get(category, "shape", "square").set(ModuleWorldBorder.shape.toString());
		config.save();
	}
}
