package com.ForgeEssentials.core.data;

/**
 * These classes "adapt" an object's data to be stored in conjunction with a
 * particular DataDriver. Each object that needs to be saved must have a DataAdapter
 * written per DataDriver.
 * 
 * This is a generic class meant to be inherited once per class per variety of
 * backend.
 * 
 * @author MysteriousAges
 *
 */
public abstract class DataAdapter<SavedType, KeyType>
{
	abstract public boolean saveData(SavedType object);
	
	abstract public boolean loadData(KeyType loadingKey, SavedType object);
}
