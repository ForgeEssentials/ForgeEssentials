package com.ForgeEssentials.data;

/**
 * These classes "adapt" an object's data to be stored in conjunction with a particular DataDriver. Each object that needs to be saved must have a IDataAdapter
 * written for its DataDriver.
 * 
 * This is a generic class meant to be inherited once per class per variety of backend.
 * 
 * @author MysteriousAges
 * 
 */
public interface IDataAdapter<SavedType, KeyType>
{
	abstract public boolean saveData(SavedType object);

	abstract public boolean loadData(KeyType uniqueObjectKey, SavedType object);

	abstract public boolean deleteData(KeyType uniqueObjectKey);
}
