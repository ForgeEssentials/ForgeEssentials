package com.ForgeEssentials.protection;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.IModuleConfig;

/**
 * This generates the configuration structure + an example file.
 * 
 * @author Dries007
 */
public class ConfigProtection implements IModuleConfig
{
	public static final File pconfig = new File(ForgeEssentials.FEDIR, "Protection.cfg");
	public Configuration config;

	public ConfigProtection()
	{
	}

	@Override
	public void setGenerate(boolean generate)
	{
		// nothing
	}

	@Override
	public void init()
	{
		config = new Configuration(pconfig, true);
		String cat = "Protection";

		config.addCustomCategoryComment(cat, "You can override the default permission values on the permissions config. (or in the database.)");
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

		config.addCustomCategoryComment(cat, "You can override the default permission values on the permissions config. (or in the database.)");
		ModuleProtection.enable = config.get(cat, "enable", true, "Guess what this does?").getBoolean(true);
	}

	@Override
	public File getFile()
	{
		return pconfig;
	}
}
