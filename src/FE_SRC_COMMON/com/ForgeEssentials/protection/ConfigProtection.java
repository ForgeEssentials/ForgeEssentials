package com.ForgeEssentials.protection;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;

/**
 * This generates the configuration structure + an example file.
 * @author Dries007
 */
public class ConfigProtection extends ModuleConfigBase
{
	public Configuration	config;

	public ConfigProtection(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file, true);
		String cat = "Protection";

		config.addCustomCategoryComment(cat, "You can override the default permission values in the permissions config. (or in the database.)");
		ModuleProtection.enable = config.get(cat, "enable", true, "Guess what this does?").getBoolean(true);

		config.save();
	}

	@Override
	public void forceSave()
	{
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();
		String cat = "Protection";

		config.addCustomCategoryComment(cat, "You can override the default permission values in the permissions config. (or in the database.)");
		ModuleProtection.enable = config.get(cat, "enable", true, "Guess what this does?").getBoolean(true);
	}
}
