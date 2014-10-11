package com.forgeessentials.permissions.core;

import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.DBConnector;
import com.forgeessentials.util.EnumDBType;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class ConfigPermissions extends ModuleConfigBase {

	protected Configuration config;

	protected DBConnector connector;

	protected boolean importBool;

	protected String importDir;

	@Override
	public void init()
	{
        connector = new DBConnector("PermissionsDB", DataStorageManager.getCoreDBConnector(), EnumDBType.H2_FILE, "FEPerms", file.getParent() + "/permissions",
            false);
		config = new Configuration(file);

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

	public DBConnector getDBConnector()
	{
		return connector;
	}

    public boolean universalConfigAllowed(){return true;}

}
