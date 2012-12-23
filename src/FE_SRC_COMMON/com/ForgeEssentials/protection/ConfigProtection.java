package com.ForgeEssentials.protection;

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
