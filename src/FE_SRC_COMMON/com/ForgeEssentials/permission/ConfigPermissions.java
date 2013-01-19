package com.ForgeEssentials.permission;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.moduleLauncher.ModuleConfigBase;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;

public class ConfigPermissions extends ModuleConfigBase
{
	protected Configuration config;

	private static boolean permDefault = false;

	public ConfigPermissions(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file);

		permDefault = config.get("stuff", "permissionDefault", false,
				"If a permission is not set anywhere, it will return this. True = allow. False = deny").getBoolean(false);
		config.get("stuff", "databaseType", "H2", " MySQL and H2 are the only supported databases at the moment.");

		config.addCustomCategoryComment("MySQL", "For everything MySQL");
		config.get("MySQL", "host", "server.example.com");
		config.get("MySQL", "port", 3306);
		config.get("MySQL", "database", "FE_Permissions", "WILL CRASH IF IT DOESN'T EXIST!  This will still be used even if StealConfigFromCore is enabled.");
		config.get("MySQL", "username", "FEUser");
		config.get("MySQL", "password", "@we$0mePa$$w0rd");
		config.get("MySQL", "stealConfigFromCore", false,
				"if this is true, the mysql details from ForgeEssentials/main.cfg will be used. The database specified here will still be used.");

		config.addCustomCategoryComment("H2", "For everything H2 (flatfile DB)");
		config.get("H2", "file", "permissions", "DO NOT put .db on the end of this file name!");
		config.get("H2", "absolutePath", false, "if this is true, the path below will be parsed as an absolute path. Otherwise it is relative to this dir.");

		config.save();
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

	public PermResult getPermDefault()
	{
		return permDefault ? PermResult.ALLOW : PermResult.DENY;
	}

}
