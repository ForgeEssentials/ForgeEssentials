package com.ForgeEssentials.data;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IDataDriver;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.api.data.TypeData;
import com.google.common.collect.HashMultimap;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractDataDriver implements IDataDriver
{
	private HashMultimap<String, String>	classRegister	= HashMultimap.create();
	private boolean							hasLoaded;

	@Override
	public void onClassRegistered(ITypeInfo tagger)
	{
	}

	@Override
	public final String getName()
	{
		return this.getClass().getSimpleName().replaceAll("DataDriver", "");
	}

	@Override
	public boolean saveObject(ClassContainer type, Object o)
	{
		boolean flag = false;

		if (!classRegister.containsEntry(getName(), type.getName()))
		{
			onClassRegistered(DataStorageManager.getInfoForType(type));
			classRegister.put(getName(), type.getName());
		}

		ITypeInfo t = DataStorageManager.getInfoForType(type);
		if (t != null)
		{
			flag = true;
			saveData(type, t.getTypeDataFromObject(o));
		}

		return flag;
	}

	@Override
	public Object loadObject(ClassContainer type, String loadingKey)
	{
		Object newObject = null;
		TypeData data = loadData(type, loadingKey);
		ITypeInfo info = DataStorageManager.getInfoForType(type);

		if (data != null && data.getAllFields().size() > 0)
		{
			newObject = createFromFields(data, info);
		}

		return newObject;
	}

	@Override
	public Object[] loadAllObjects(ClassContainer type)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		TypeData[] objectData = loadAll(type);
		ITypeInfo info = DataStorageManager.getInfoForType(type);

		// Each element of the field array represents an object, stored as an
		// array of fields.
		Object tmp;
		if (objectData != null && objectData.length > 0)
		{
			for (TypeData data : objectData)
			{
				tmp = createFromFields(data, info);
				list.add(tmp);
			}
		}

		return list.toArray(new Object[list.size()]);
	}

	@Override
	public boolean deleteObject(ClassContainer type, String loadingKey)
	{
		return deleteData(type, loadingKey);
	}

	private Object createFromFields(TypeData data, ITypeInfo info)
	{
		Object val;
		// loops through all fields of this class.
		for (Entry<String, Object> entry : data.getAllFields())
		{
			// if it needs reconstructing before this class...
			if (entry.getValue() instanceof TypeData)
			{
				// reconstruct the class...
				val = createFromFields((TypeData) entry.getValue(), info.getInfoForField(entry.getKey()));

				// re-add it to the map.
				data.putField(entry.getKey(), val);
			}
		}

		// actually reconstruct this class
		val = info.reconstruct(data);

		// return the reconstructed value.
		return val;
	}

	@Override
	public void parseConfigs(Configuration config, String category) throws Exception
	{
		loadFromConfigs(config, category);
		hasLoaded = true;
	}

	@Override
	public boolean hasLoaded()
	{
		return hasLoaded;
	}

	abstract public void loadFromConfigs(Configuration config, String category) throws Exception;

	abstract protected boolean saveData(ClassContainer type, TypeData fieldList);

	abstract protected TypeData loadData(ClassContainer type, String uniqueKey);

	abstract protected TypeData[] loadAll(ClassContainer type);

	abstract protected boolean deleteData(ClassContainer type, String uniqueObjectKey);
}
