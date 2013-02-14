package com.ForgeEssentials.api.data;

import net.minecraftforge.common.Configuration;


public interface IDataDriver
{
	void onClassRegistered(ITypeInfo tagger);

	String getName();

	boolean saveObject(Object o);

	Object loadObject(Class type, String loadingKey);

	Object[] loadAllObjects(Class type);

	boolean deleteObject(Class type, String loadingKey);

	void parseConfigs(Configuration config, String category, String worldName) throws Exception;

	EnumDriverType getType();
}
