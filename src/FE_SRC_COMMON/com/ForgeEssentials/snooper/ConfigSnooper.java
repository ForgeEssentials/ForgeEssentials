package com.ForgeEssentials.snooper;

import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.ForgeEssentials.api.snooper.Response;

import net.minecraft.command.ICommandSender;

import net.minecraftforge.common.Configuration;

import java.io.File;

public class ConfigSnooper extends ModuleConfigBase
{	
	private Configuration config;
	
	public ConfigSnooper(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file, true);

		String cat = "Snooper";

		ModuleSnooper.port = config.get(cat, "port", 25565, "The query port").getInt();
		ModuleSnooper.hostname = config.get(cat, "hostname", "", "The query hostname/IP").value;

		ModuleSnooper.autoReboot = config.get(cat, "autoReload", true, "Automatically reload the query system if a fatal error occurs").getBoolean(true);
		ModuleSnooper.enable = config.get(cat, "enable", false, "This one is obvious, don't you think?").getBoolean(false);

		for (Response response : ResponseRegistry.getAllresponses())
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

		config.get(cat, "port", 25565, "").value = "" + ModuleSnooper.port;
		config.get(cat, "hostname", "", "The query hostname/IP").value = ModuleSnooper.hostname;

		config.get(cat, "autoReload", true, "Automatically reload the query system if a fatal error occurs").value = "" + ModuleSnooper.autoReboot;
		config.get(cat, "enable", false, "This one is obvious, don't you think?").value = "" + ModuleSnooper.enable;

		for (Response response : ResponseRegistry.getAllresponses())
		{
			String subCat = cat + "." + response.getName();
			config.get(subCat, "enable", true, "If false, this response won't be allowed on this server.").value = "" + response.allowed;
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

		ModuleSnooper.autoReboot = config.get(cat, "autoReload", true, "Automatically reload the query system if a fatal error occurs").getBoolean(true);
		ModuleSnooper.enable = config.get(cat, "enable", false, "This one is obvious, don't you think?").getBoolean(false);

		for (Response response : ResponseRegistry.getAllresponses())
		{
			String subCat = cat + "." + response.getName();
			response.allowed = config.get(subCat, "enable", true, "If false, this response won't be allowed on this server.").getBoolean(true);
			response.readConfig(subCat, config);
		}
		ModuleSnooper.startQuery();
	}
}