package com.ForgeEssentials.api.data;

import net.minecraftforge.common.Configuration;

public interface IDataDriver
{
	void onClassRegistered(ITypeInfo tagger);

	String getName();

	boolean saveObject(ClassContainer type, Object o);

	Object loadObject(ClassContainer type, String loadingKey);

	Object[] loadAllObjects(ClassContainer type);

	boolean deleteObject(ClassContainer type, String loadingKey);

	void parseConfigs(Configuration config, String category, String worldName) throws Exception;

	EnumDriverType getType();

	public boolean hasLoaded();
}
