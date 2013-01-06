package com.ForgeEssentials.data;

import java.util.ArrayList;

import net.minecraftforge.common.Configuration;

public abstract class DataDriver
{

	public DataDriver()
	{
	}

	public void onClassRegisterred(TypeTagger tagger)
	{

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
			newObject = DataStorageManager.taggerList.get(type).createFromFields(data);
		}

		return newObject;
	}

	public Object[] loadAllObjects(Class type)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		TaggedClass[] objectData = loadAll(type);

		// Each element of the field array represents an object, stored as an
		// array of fields.
		if (objectData != null && objectData.length > 0)
		{
			for (TaggedClass tag : objectData)
			{
				list.add(loadObject(type, tag.uniqueKey));
			}
		}

		return list.toArray(new Object[] {});
	}

	public boolean deleteObject(Class type, Object loadingKey)
	{
		return deleteData(type, loadingKey);
	}

	abstract public void parseConfigs(Configuration config, String worldName) throws Exception;

	abstract protected boolean saveData(Class type, TaggedClass fieldList);

	abstract protected TaggedClass loadData(Class type, Object uniqueKey);

	abstract protected TaggedClass[] loadAll(Class type);

	abstract protected boolean deleteData(Class type, Object uniqueObjectKey);
}
