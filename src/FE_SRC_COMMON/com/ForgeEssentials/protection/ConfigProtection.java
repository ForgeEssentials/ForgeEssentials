package com.ForgeEssentials.protection;

import java.io.File;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;

/**
 * This generates the configuration structure + an example file.
 * 
 * @author Dries007
 *
 */

public class ConfigProtection 
{
	public static final File pconfig = new File(ForgeEssentials.FEDIR, "WorldBorder.cfg");
	public final Configuration config;
	
	public ConfigProtection()
	{
		config = new Configuration(pconfig, true);
		
		
		
		config.save();
	}
}
