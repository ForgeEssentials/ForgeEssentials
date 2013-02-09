package com.ForgeEssentials.api.data;

import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.EnumDriverType;
import com.ForgeEssentials.data.TypeTagger;
import com.ForgeEssentials.util.DBConnector;

public interface IStorageManager
{
	void registerDriver(String name, Class<? extends DataDriver> c);

	DataDriver getReccomendedDriver();

	DataDriver getDriverOfType(EnumDriverType type);

	void registerSaveableClass(Class type);

	boolean hasMapping(Class type);

	TypeTagger getTaggerForType(Class type);

	DBConnector getCoreDBConnector();
}
