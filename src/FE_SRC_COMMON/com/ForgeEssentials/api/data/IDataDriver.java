package com.ForgeEssentials.api.data;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public interface IDataDriver
{
	void onClassRegistered(ITypeInfo tagger);

	String getName();

	boolean saveObject(ClassContainer type, Object o);

	Object loadObject(ClassContainer type, String loadingKey);

	Object[] loadAllObjects(ClassContainer type);

	boolean deleteObject(ClassContainer type, String loadingKey);

	void parseConfigs(Configuration config, String category) throws Exception;
	
	void serverStart(FMLServerStartingEvent e);

	EnumDriverType getType();

	public boolean hasLoaded();
}
