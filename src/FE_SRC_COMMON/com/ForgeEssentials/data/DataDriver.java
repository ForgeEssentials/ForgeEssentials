package com.ForgeEssentials.data;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IDataDriver;

import java.util.ArrayList;

import net.minecraftforge.common.Configuration;

public abstract class DataDriver implements IDataDriver
{

	public DataDriver()
	{
	}

	public void onClassRegistered(TypeTagger tagger)
	{

	}
	
	public final String getName()
	{
		return this.getClass().getSimpleName().replace(DataDriver.class.getSimpleName(), "");
	}

	public boolean saveObject(Object o)
	{
		boolean flag = false;

		TypeTagger t;
		if ((t = DataStorageManager.getTaggerForType(o.getClass())) != null)
		{
			flag = true;
			saveData(o.getClass(), t.getTaggedClassFromObject(o));
		}

		return flag;
	}

	public Object loadObject(Class type, Object loadingKey)
	{
		Object newObject = null;
		TaggedClass data = loadData(type, loadingKey);

		if (data != null)
		{
			newObject = StorageManager.taggerList.get(type).createFromFields(data);
		}

		return newObject;
	}

	public Object[] loadAllObjects(Class type)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		TaggedClass[] objectData = loadAll(type);

		// Each element of the field array represents an object, stored as an
		// array of fields.
		Object tmp;
		if (objectData != null && objectData.length > 0)
		{
			for (TaggedClass tag : objectData)
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

	abstract protected boolean saveData(Class type, TaggedClass fieldList);

	abstract protected TaggedClass loadData(Class type, Object uniqueKey);

	abstract protected TaggedClass[] loadAll(Class type);

	abstract protected boolean deleteData(Class type, Object uniqueObjectKey);
	
	abstract public EnumDriverType getType();
}
