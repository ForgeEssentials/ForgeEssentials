package com.ForgeEssentials.api.data;

import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.util.DBConnector;

public interface IStorageManager
{
	void registerDriver(String name, Class<? extends DataDriver> c);

	DataDriver getReccomendedDriver();

	DataDriver getDriverOfType(EnumDriverType type);

	/**
	 * Registers the class and registers the provided TypeInfo for it.
	 * @param infoType
	 * @param type
	 */
	void registerSaveableClass(Class<? extends ITypeInfo> infoType, Class type);

	/**
	 * Registers the class as a SaveableClass to be read with the Annotations.
	 * This method should only be used for classes that use the default ITypeInfo and have the annotations.
	 * @param type The class to register
	 */
	void registerSaveableClass(Class type);

	ITypeInfo getInfoForType(Class type);

	TypeData getDataForType(Class type);

	TypeData getDataForObject(Object obj);

	DBConnector getCoreDBConnector();
}
