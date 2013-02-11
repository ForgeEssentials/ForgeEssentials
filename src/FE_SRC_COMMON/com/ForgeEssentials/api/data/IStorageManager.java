package com.ForgeEssentials.api.data;

import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.EnumDriverType;
import com.ForgeEssentials.data.TypeInfoWrapper;
import com.ForgeEssentials.util.DBConnector;

public interface IStorageManager
{
	void registerDriver(String name, Class<? extends DataDriver> c);

	DataDriver getReccomendedDriver();

	DataDriver getDriverOfType(EnumDriverType type);
	
	void registerSaveableClass(Class<? extends ITypeInfo> infoType, Class type);
	
	void registerSaveableClass(Class type);

	ITypeInfo getInfoForType(Class type);
	
	AbstractTypeData getDataForType(Class type);
	
	AbstractTypeData getDataForObject(Object obj);

	DBConnector getCoreDBConnector();
}
