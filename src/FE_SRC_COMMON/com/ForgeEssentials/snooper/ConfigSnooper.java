package com.ForgeEssentials.snooper;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.ForgeEssentials.api.snooper.Response;

public class ConfigSnooper extends ModuleConfigBase
{
	private Configuration	config;

	public ConfigSnooper(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file, true);

		String cat = "Snooper";

		ModuleSnooper.port = config.get(cat, "port", 25566, "The query port. It cannot be the same as the server.").getInt();
		ModuleSnooper.hostname = config.get(cat, "hostname", "", "The query hostname/IP").value;

		ModuleSnooper.enable = config.get(cat, "enable", false, "This one is obvious, don't you think?").getBoolean(false);

		for (Response response : ResponseRegistry.getAllresponses())
		{
			String subCat = cat + "." + response.getName();
			response.allowed = config.get(subCat, "enable", true, "If false, this response won't be allowed on this server.").getBoolean(true);
			response.readConfig(subCat, config);
		}

		config.save();
	}

	@Override
	public void forceSave()
	{
		String cat = "Snooper";

		config.get(cat, "port", 25565, "").value = "" + ModuleSnooper.port;
		config.get(cat, "hostname", "", "The query hostname/IP").value = ModuleSnooper.hostname;

		config.get(cat, "enable", false, "This one is obvious, don't you think?").value = "" + ModuleSnooper.enable;

		config.get(cat, "keysize", 128, "AES Keysize. Only affects new keys.").value = ModuleSnooper.keysize + "";

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

		ModuleSnooper.enable = config.get(cat, "enable", false, "This one is obvious, don't you think?").getBoolean(false);

		ModuleSnooper.keysize = config.get(cat, "keysize", 128, "AES Keysize. Only affects new keys.").getInt();

		for (Response response : ResponseRegistry.getAllresponses())
		{
			String subCat = cat + "." + response.getName();
			response.allowed = config.get(subCat, "enable", true, "If false, this response won't be allowed on this server.").getBoolean(true);
			response.readConfig(subCat, config);
		}
	}
}
