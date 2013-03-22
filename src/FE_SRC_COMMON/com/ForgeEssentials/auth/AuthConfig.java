package com.ForgeEssentials.auth;

import java.io.File;
import java.util.HashSet;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;

public class AuthConfig extends ModuleConfigBase
{
	private Configuration		config;
	private static final String	CATEGORY_MAIN	= "main";

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
		ModuleAuth.allowOfflineReg = config.get(CATEGORY_MAIN, "allowOfflineReg", false, "Allow peopls to register usernames while server is offline. Don't allow this for primarily Online servers.").getBoolean(false);
		ModuleAuth.salt = config.get(CATEGORY_MAIN, "salt", ModuleAuth.salt, "The salt to be used when hashing passwords").value;

		config.save();
	}

	@Override
	public void forceSave()
	{
		config.get(CATEGORY_MAIN, "forceEnable", false, "Forces the module to be loaded regardless of Minecraft auth services").value = "" + ModuleAuth.forceEnabled;
		config.get(CATEGORY_MAIN, "autoEnable", true, "Enables the module if and when the Minecraft Auth servers go down.").value = "" + ModuleAuth.checkVanillaAuthStatus;
		config.get(CATEGORY_MAIN, "allowOfflineReg", false, "Allow registration while server is offline. Don't allow this.").value = "" + ModuleAuth.allowOfflineReg;
		config.get(CATEGORY_MAIN, "salt", "", "The salt to be used when hashing passwords").value = ModuleAuth.salt;

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();
		ModuleAuth.forceEnabled = config.get(CATEGORY_MAIN, "forceEnable", false).getBoolean(false);
		ModuleAuth.checkVanillaAuthStatus = config.get(CATEGORY_MAIN, "autoEnable", true).getBoolean(false);
		ModuleAuth.allowOfflineReg = config.get(CATEGORY_MAIN, "allowOfflineReg", false).getBoolean(false);
		ModuleAuth.salt = config.get(CATEGORY_MAIN, "salt", ModuleAuth.salt).value;
	}

}
