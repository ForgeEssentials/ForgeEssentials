package com.ForgeEssentials.data;

import java.util.*;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.util.OutputHandler;


public abstract class DataDriver
{
	private HashMap<Class, TypeTagger> taggerList;
	
	public DataDriver()
	{
		this.taggerList = new HashMap<Class, TypeTagger>();
	}

	public Class getDataDriverType()
	{
		return this.getClass();
	}

	public boolean registerClass(Class type)
	{
		boolean flag = false;
		SaveableObject a;
		if ((a = (SaveableObject)type.getAnnotation(SaveableObject.class)) != null)
		{
			// Create a tagger for this object and save it in our hashmap.
			this.taggerList.put(type, new TypeTagger(this, type));
		}
		return flag;
	}

	public boolean hasMapping(Object o)
	{
		return this.taggerList.containsKey(o.getClass());
	}
	
	public boolean hasMapping(Class type)
	{
		return this.taggerList.containsKey(type);
	}
	
	public TypeTagger getTaggerForType(Class type)
	{
		if (!this.hasMapping(type))
		{
			this.registerClass(type);
		}
		return this.taggerList.get(type);
	}

	public boolean saveObject(Object o)
	{
		boolean flag = false;
		
		TypeTagger t;
		if ((t = getTaggerForType(o.getClass())) != null)
		{
			flag = true;
			this.saveData(o.getClass(), t.getTaggedClassFromObject(o));
		}
		
		return flag;
	}
	
	@Deprecated
	public boolean loadObject(String type, Object loadingKey)
	{
		return false;
	}

	public Object loadObject(Class type, Object loadingKey)
	{
		Object newObject = null;
		TaggedClass data = this.loadData(type, loadingKey);

		if (data != null)
		{
			newObject = this.taggerList.get(type).createFromFields(data);
		}
		
		return newObject;
	}
	
	public Object[] loadAllObjects(Class type)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		TaggedClass[] objectData = loadAll(type);
		
		// Each element of the field array represents an object, stored as an array of fields.
		if (objectData != null && objectData.length > 0)
		{
			
		}

		return list.toArray(new Object[list.size()]);
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
