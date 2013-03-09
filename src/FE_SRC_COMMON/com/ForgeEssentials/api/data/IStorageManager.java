package com.ForgeEssentials.api.data;

import com.ForgeEssentials.data.AbstractDataDriver;
import com.ForgeEssentials.util.DBConnector;

@SuppressWarnings("rawtypes")
public interface IStorageManager
{
	void registerDriver(String name, Class<? extends AbstractDataDriver> c);

	AbstractDataDriver getReccomendedDriver();

	AbstractDataDriver getDriverOfType(EnumDriverType type);

	/**
	 * Registers the class and registers the provided TypeInfo for it.
	 * @param infoType
	 * @param type
	 */
	void registerSaveableClass(Class<? extends ITypeInfo> infoType, ClassContainer type);

	/**
	 * Registers the class as a SaveableClass to be read with the Annotations.
	 * This method should only be used for classes that use the default ITypeInfo and have the annotations.
	 * @param type The class to register
	 */
	void registerSaveableClass(ClassContainer type);

	ITypeInfo getInfoForType(ClassContainer type);

	TypeData getDataForType(ClassContainer type);

	TypeData getDataForObject(ClassContainer container, Object obj);

	DBConnector getCoreDBConnector();
}
