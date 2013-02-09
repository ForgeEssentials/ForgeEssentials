package com.ForgeEssentials.api.data;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.data.EnumDriverType;
import com.ForgeEssentials.data.TypeTagger;

public interface IDataDriver
{
	void onClassRegistered(TypeTagger tagger);
	
	String getName();
	
	boolean saveObject(Object o);
	
	Object loadObject(Class type, Object loadingKey);
	
	Object[] loadAllObjects(Class type);
	
	boolean deleteObject(Class type, Object loadingKey);
	
	void parseConfigs(Configuration config, String category, String worldName) throws Exception;
	
	EnumDriverType getType();
}
