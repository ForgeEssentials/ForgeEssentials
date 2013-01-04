package com.ForgeEssentials.snooper;

import java.io.File;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IModuleConfig;
import com.ForgeEssentials.snooper.API.Response;

import net.minecraft.command.ICommandSender;

public class ConfigSnooper implements IModuleConfig
{
	private static final File file = new File(ForgeEssentials.FEDIR, "Snooper.cfg");
	private Configuration config;
	
	public ConfigSnooper()
	{
		// nothing
	}

	@Override
	public void setGenerate(boolean generate)
	{
		// nothing
	}

	@Override
	public void init()
	{
		config = new Configuration(file, true);
		
		String cat = "Snooper";
		
		ModuleSnooper.port = config.get(cat, "port", 25565, "The query port").getInt();
		ModuleSnooper.hostname = config.get(cat, "hostname", "", "The query hostname/IP").value;
		
		ModuleSnooper.autoReboot = config.get(cat, "autoReload", true, "Automaticly reload the query system if a fatal error occures").getBoolean(true);
		ModuleSnooper.enable = config.get(cat, "enable", false, "This one is obvious don't you think?").getBoolean(false);
		
		for(Response response : ResponseRegistry.getAllresponses())
		{
			String subCat = cat + "." + response.getName();
			response.allowed = config.get(subCat, "enable", true, "If false, this response won't be allowed on this server.").getBoolean(true);
			response.readConfig(subCat, config);
		}
		config.save();
		ModuleSnooper.startQuery();
	}

	@Override
	public void forceSave()
	{
		String cat = "Snooper";
		
		config.get(cat, "port", 25565, "").value = ""+ModuleSnooper.port;
		config.get(cat, "hostname", "", "The query hostname/IP").value = ModuleSnooper.hostname;
		
		config.get(cat, "autoReload", true, "Automaticly reload the query system if a fatal error occures").value = ""+ModuleSnooper.autoReboot;
		config.get(cat, "enable", false, "This one is obvious don't you think?").value = ""+ModuleSnooper.enable;
		
		for(Response response : ResponseRegistry.getAllresponses())
		{
			String subCat = cat + "." + response.getName();
			config.get(subCat, "enable", true, "If false, this response won't be allowed on this server.").value = ""+response.allowed;
			response.writeConfig(subCat, config);
		}
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();
		
		String cat = "Snooper";
		
		ModuleSnooper.port = config.get(cat, "port", 25565, "The query port").getInt();
		ModuleSnooper.hostname = config.get(cat, "hostname", "", "The query hostname/IP").value;
		
		ModuleSnooper.autoReboot = config.get(cat, "autoReload", true, "Automaticly reload the query system if a fatal error occures").getBoolean(true);
		ModuleSnooper.enable = config.get(cat, "enable", false, "This one is obvious don't you think?").getBoolean(false);
		
		for(Response response : ResponseRegistry.getAllresponses())
		{
			String subCat = cat + "." + response.getName();
			response.allowed = config.get(subCat, "enable", true, "If false, this response won't be allowed on this server.").getBoolean(true);
			response.readConfig(subCat, config);
		}
		ModuleSnooper.startQuery();
	}

	@Override
	public File getFile()
	{
		return file;
	}
}