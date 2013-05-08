package com.ForgeEssentials.permission;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.core.moduleLauncher.ModuleConfigBase;
import com.ForgeEssentials.data.api.DataStorageManager;
import com.ForgeEssentials.util.DBConnector;
import com.ForgeEssentials.util.EnumDBType;

public class ConfigPermissions extends ModuleConfigBase
{
	protected Configuration	config;
	protected DBConnector	connector;
	protected boolean		importBool;
	protected String		importDir;

	private static boolean	permDefault	= false;

	public ConfigPermissions(File file)
	{
		super(file);
		connector = new DBConnector("PermissionsDB", DataStorageManager.getCoreDBConnector(), EnumDBType.H2_FILE, "FEPerms", file.getParent() + "/permissions", false);
	}

	@Override
	public void init()
	{
		config = new Configuration(file);

		permDefault = config.get("stuff", "permissionDefault", false, "If a permission is not set anywhere, it will return this. True = allow. False = deny").getBoolean(false);

		importBool = config.get("stuff", "import", false, "if permissions should be imported from the specified dir").getBoolean(false);
		importDir = config.get("stuff", "importDir", "import", "file from wich permissions should be imported").getString();

		if (importBool == true)
		{
			config.get("stuff", "import", false).set(false);
		}

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
