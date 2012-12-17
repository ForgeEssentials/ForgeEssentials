package com.ForgeEssentials.data;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.common.Configuration;

public abstract class DataDriver
{
	private HashMap<Class, TypeTagger>	taggerList;

	public DataDriver()
	{
		taggerList = new HashMap<Class, TypeTagger>();
	}

	public Class getDataDriverType()
	{
		return this.getClass();
	}

	public void registerClass(Class type)
	{
		assert type.isAnnotationPresent(SaveableObject.class) : new IllegalArgumentException("Only classes that have the @SaveableObject annotation may be registerred!");
		taggerList.put(type, new TypeTagger(this, type));
	}

	public boolean hasMapping(Object o)
	{
		return taggerList.containsKey(o.getClass());
	}

	public boolean hasMapping(Class type)
	{
		return taggerList.containsKey(type);
	}

	public TypeTagger getTaggerForType(Class type)
	{
		if (!this.hasMapping(type))
			registerClass(type);
		return taggerList.get(type);
	}

	public boolean saveObject(Object o)
	{
		boolean flag = false;

		TypeTagger t;
		if ((t = getTaggerForType(o.getClass())) != null)
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
			newObject = taggerList.get(type).createFromFields(data);

		return newObject;
	}

	public Object[] loadAllObjects(Class type)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		TaggedClass[] objectData = loadAll(type);

		// Each element of the field array represents an object, stored as an array of fields.
		if (objectData != null && objectData.length > 0)
			for (TaggedClass tag: objectData)
				list.add(loadObject(type, tag.LoadingKey.Value));

		return list.toArray(new Object[] {});
	}

	public boolean deleteObject(Class type, Object loadingKey)
	{
		return deleteData(type, loadingKey);
	}

	abstract public boolean parseConfigs(Configuration config, String worldName);

	abstract protected boolean saveData(Class type, TaggedClass fieldList);

	abstract protected TaggedClass loadData(Class type, Object uniqueKey);

	abstract protected TaggedClass[] loadAll(Class type);

	abstract protected boolean deleteData(Class type, Object uniqueObjectKey);
}
