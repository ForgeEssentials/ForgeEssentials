package com.ForgeEssentials.data;

import java.util.ArrayList;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IDataDriver;

public abstract class DataDriver implements IDataDriver
{

	public DataDriver()
	{
	}

	public void onClassRegistered(TypeInfoHandler tagger)
	{

	}

	public final String getName()
	{
		return this.getClass().getSimpleName().replace(DataDriver.class.getSimpleName(), "");
	}

	public boolean saveObject(Object o)
	{
		boolean flag = false;

		TypeInfoHandler t;
		if ((t = DataStorageManager.getInfoForType(o.getClass())) != null)
		{
			flag = true;
			saveData(o.getClass(), t.getTypeDataFromObject(o));
		}

		return flag;
	}

	public Object loadObject(Class type, Object loadingKey)
	{
		Object newObject = null;
		TypeData data = loadData(type, loadingKey);

		if (data != null)
		{
			newObject = StorageManager.taggerList.get(type).createFromFields(data);
		}

		return newObject;
	}

	public Object[] loadAllObjects(Class type)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		TypeData[] objectData = loadAll(type);

		// Each element of the field array represents an object, stored as an
		// array of fields.
		Object tmp;
		if (objectData != null && objectData.length > 0)
		{
			for (TypeData tag : objectData)
			{
				tmp = StorageManager.taggerList.get(type).createFromFields(tag);
				list.add(tmp);
			}
		}

		return list.toArray(new Object[list.size()]);
	}

	public boolean deleteObject(Class type, Object loadingKey)
	{
		return deleteData(type, loadingKey);
	}

	abstract public void parseConfigs(Configuration config, String category, String worldName) throws Exception;

	abstract protected boolean saveData(Class type, TypeData fieldList);

	abstract protected TypeData loadData(Class type, Object uniqueKey);

	abstract protected TypeData[] loadAll(Class type);

	abstract protected boolean deleteData(Class type, Object uniqueObjectKey);

	abstract public EnumDriverType getType();
}
