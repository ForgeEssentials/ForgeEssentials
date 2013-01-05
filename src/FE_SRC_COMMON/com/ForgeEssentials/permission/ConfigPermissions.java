package com.ForgeEssentials.permission;

import com.ForgeEssentials.core.IModuleConfig;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;

import net.minecraft.command.ICommandSender;

import net.minecraftforge.common.Configuration;

import java.io.File;

public class ConfigPermissions implements IModuleConfig
{
	private Configuration config;
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
		config.get("MySQL", "host", "localhost");
		config.get("MySQL", "port", 3306);
		config.get("MySQL", "database", "FE_Permissions", "If it doesn't exist.. the user will need permisisons to make it.");
		config.get("MySQL", "username", "FEUser");
		config.get("MySQL", "password", "blahblah");
		config.get("MySQL", "StealConfigFromCore", false, "if this is true.. all the mySQL db stuff here will be ignored.. and the stuff from the CORE config will be used instead.");
		
		config.addCustomCategoryComment("SqLite", "For everything SqLite");
		config.get("SqLite", "file", "permissions.db");
		config.get("SqLite", "absolutePath", false, "if this is true, the below path will be parsed as an absolute path. otherwise it is relative to this dir.");
	}

	@Override
	public void forceSave()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public File getFile()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public PermResult getPermDefault()
	{
		return permDefault ? PermResult.ALLOW : PermResult.DENY;
	}
	
	public String getConnectionString()
	{
		String type = config.get("stuff", "databaseType", "SqLite").value;
		
		StringBuilder connect = new StringBuilder("jdbc:");
		
		return null;
	}

}
