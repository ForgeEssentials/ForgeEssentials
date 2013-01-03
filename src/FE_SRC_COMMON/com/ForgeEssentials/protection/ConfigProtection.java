package com.ForgeEssentials.protection;

import java.io.File;
import java.util.HashMap;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.permission.PermissionsAPI;

/**
 * This generates the configuration structure + an example file.
 * 
 * @author Dries007
 *
 */

public class ConfigProtection 
{
	public static final File pconfig = new File(ForgeEssentials.FEDIR, "Protection.cfg");
	public final Configuration config;
	
	public ConfigProtection()
	{
		config = new Configuration(pconfig, true);
		String cat = "Protection";
		
		config.addCustomCategoryComment(cat, "You can override the default permission values on the permissions config. (or in the database.)");
		ModuleProtection.enable = config.get(cat, "enable", true, "Guess what this does?").getBoolean(true);
		
		config.save();
	}
}
