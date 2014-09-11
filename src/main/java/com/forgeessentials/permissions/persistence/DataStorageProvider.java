package com.forgeessentials.permissions.persistence;

import java.io.File;

import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permissions.core.IZonePersistenceProvider;

public class DataStorageProvider implements IZonePersistenceProvider {

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
