package com.ForgeEssentials.auth;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;

public class AuthConfig extends ModuleConfigBase
{
	private Configuration		config;
	
	private static final String CATEGORY_MAIN = "main";
	private static final String CATEGORY_DB = "DatabaseStuff";
	private static final String CATEGORY_ENCRYPT = "Encryption stuff";

	public AuthConfig(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file);
		
		config.addCustomCategoryComment("main", "all the main important stuff");
		ModuleAuth.enabled = ModuleAuth.forceEnabled = config.get(CATEGORY_MAIN, "forceEnable", false, "Forces the module to be loaded regardless of Minecraft auth services").getBoolean(false);
		ModuleAuth.checkVanillaAuthStatus = config.get(CATEGORY_MAIN, "autoEnable", true, "Enables the module if and when the Minecraft Auth servers go down.").getBoolean(false);
		
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
	}

}
