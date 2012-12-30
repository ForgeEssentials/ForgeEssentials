package com.ForgeEssentials.snooper;

import java.io.File;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.snooper.API.Response;

public class ConfigSnooper 
{
	public static final File wbconfig = new File(ForgeEssentials.FEDIR, "Snooper.cfg");
	public final Configuration config;
	
	public ConfigSnooper()
	{
		config = new Configuration(wbconfig, true);
		
		String cat = "Snooper";
		
		ModuleSnooper.port = config.get(cat, "port", 25565, "The query port").getInt();
		ModuleSnooper.hostname = config.get(cat, "hostname", "", "The query hostname/IP").value;
		
		ModuleSnooper.autoReboot = config.get(cat, "autoReload", true, "Automaticly reload the query system if a fatal error occures").getBoolean(true);
		ModuleSnooper.enable = config.get(cat, "enable", false, "This one is obvious don't you think?").getBoolean(false);
		
		for(Response response : ResponseRegistry.getAllresponses())
		{
			String subCat = cat + "." + response.getName();
			response.allowed = config.get(subCat, "enable", true, "If false, this response won't be allowed on this server.").getBoolean(true);
			response.setupConfig(subCat, config);
		}
		config.save();
		
		
		// Here to make /reload work
		ModuleSnooper.startQuery();
	}
}