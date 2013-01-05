package com.ForgeEssentials.permission;

import com.ForgeEssentials.core.IModuleConfig;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;

import net.minecraftforge.common.Configuration;

import java.io.File;

public class ConfigPermissions implements IModuleConfig
{
	protected Configuration config;
	private File file;
	
	private static boolean permDefault = false;
	
	public ConfigPermissions()
	{
		file = new File(ModulePermissions.permsFolder, "config.cfg");
	}

	@Override
	public void setGenerate(boolean generate)
	{
		// idc....
	}

	@Override
	public void init()
	{
		config = new Configuration(file);
		
		permDefault = config.get("stuff", "permissionDefault", false, "if a permission is not set anywhere.. it will be return this. True = allow  False == deny").getBoolean(false);
		config.get("stuff", "databaseType", "SqLite", " MySQL and SqLite are the only ones supported atm.");
		
		config.addCustomCategoryComment("MySQL", "For everything MySQL");
		config.get("MySQL", "host", "server.example.com");
		config.get("MySQL", "port", 3306);
		config.get("MySQL", "database", "FE_Permissions", "WILL CRASH IF IT DOESN'T EXIST!  This will still be used even if StealConfigFromCore is enabled.");
		config.get("MySQL", "username", "FEUser");
		config.get("MySQL", "password", "@we$0mePa$$w0rd");
		config.get("MySQL", "stealConfigFromCore", false, "if this is true, the mysql details from ForgeEssentials/main.cfg will be used. The database specified here wills till be used.");
		
		config.addCustomCategoryComment("SqLite", "For everything SqLite");
		config.get("SqLite", "file", "permissions.db");
		config.get("SqLite", "absolutePath", false, "if this is true, the below path will be parsed as an absolute path. otherwise it is relative to this dir.");
		
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

	@Override
	public File getFile()
	{
		return file;
	}
	
	public PermResult getPermDefault()
	{
		return permDefault ? PermResult.ALLOW : PermResult.DENY;
	}

}
