package com.ForgeEssentials.protection;

import java.io.File;
import java.util.HashMap;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IModuleConfig;
import com.ForgeEssentials.permission.PermissionsAPI;

/**
 * This generates the configuration structure + an example file.
 * 
 * @author Dries007
 *
 */

public class ConfigProtection implements IModuleConfig 
{
	public static final File pconfig = new File(ForgeEssentials.FEDIR, "Protection.cfg");
	public Configuration config;

	@Override
	public void setGenerate(boolean generate) {}

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
	public void forceSave() {}

	@Override
	public void forceLoad(ICommandSender sender) 
	{
		sender.sendChatToPlayer("Changes to " + pconfig.getName() + " don't have effect untill next restart.");
	}

	@Override
	public File getFile()
	{
		return pconfig;
	}
}
