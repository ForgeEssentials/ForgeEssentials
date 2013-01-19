package com.ForgeEssentials.permission;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.moduleLauncher.ModuleConfigBase;
import com.ForgeEssentials.data.DataStorageManager;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;
import com.ForgeEssentials.util.DBConnector;
import com.ForgeEssentials.util.EnumDBType;

public class ConfigPermissions extends ModuleConfigBase
{
	protected Configuration config;
	protected DBConnector connector;

	private static boolean permDefault = false;

	public ConfigPermissions(File file)
	{
		super(file);
		connector = new DBConnector("PermissionsDB", DataStorageManager.getCoreDBConnector(), EnumDBType.H2_FILE, "FEPerms", "/"+file.getParent()+"/permissions", false);
	}

	@Override
	public void init()
	{
		config = new Configuration(file);

		permDefault = config.get("stuff", "permissionDefault", false,
				"If a permission is not set anywhere, it will return this. True = allow. False = deny").getBoolean(false);
		config.get("stuff", "databaseType", "H2", " MySQL and H2 are the only supported databases at the moment.");
		
		connector.loadOrGenerate(config, "database");
		
		config.save();
	}

	@Override
	public void forceSave()
	{
		connector.write(config, "database");
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		connector.loadOrGenerate(config, "database");
		config.load();
	}

	public PermResult getPermDefault()
	{
		return permDefault ? PermResult.ALLOW : PermResult.DENY;
	}

}
