package com.ForgeEssentials.core.data;

/**
 * This class defines some basic functions required for both flat-file and
 * MySQL or SQL-Lite backed data storage.
 * 
 * This is a generic class meant to be inherited once per class per variety of
 * backend.
 * 
 * @author MysteriousAges
 *
 */
public abstract class DataPersister<T>
{
	abstract public void saveData(T object);
	
	abstract public void loadData(Object loadingKey, T object);
}
