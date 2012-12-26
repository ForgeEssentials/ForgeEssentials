package com.ForgeEssentials.snooper;

import java.io.File;

import com.ForgeEssentials.core.ForgeEssentials;

import net.minecraftforge.common.Configuration;

public class ConfigSnooper 
{
	public static final File wbconfig = new File(ForgeEssentials.FEDIR, "Snooper.cfg");
	public final Configuration config;
	
	public ConfigSnooper()
	{
		config = new Configuration(wbconfig, true);
		
		String cat = "Snooper";
		
		ModuleSnooper.port = config.get(cat, "port", 25565).getInt();
		ModuleSnooper.enable = config.get(cat, "enable", false).getBoolean(false);
		
		config.save();
	}
}
