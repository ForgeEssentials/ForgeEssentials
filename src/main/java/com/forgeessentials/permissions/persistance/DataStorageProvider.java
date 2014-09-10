package com.forgeessentials.permissions.persistance;

import java.io.File;

import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permissions.core.ZonePersistanceProvider;

public class DataStorageProvider extends ZonePersistanceProvider {

	private File path;
	
	public DataStorageProvider(File path)
	{
	}

	@Override
	public void save(ServerZone serverZone)
	{
		DataStorageManager.getReccomendedDriver().saveObject(new ClassContainer(ServerZone.class), serverZone);
	}

	@Override
	public ServerZone load()
	{
		return (ServerZone) DataStorageManager.getReccomendedDriver().loadObject(new ClassContainer(ServerZone.class), "1");
	}

}
