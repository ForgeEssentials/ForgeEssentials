package com.ForgeEssentials.data.typeInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.data.api.ClassContainer;
import com.ForgeEssentials.data.api.TypeData;
import com.ForgeEssentials.data.api.TypeMultiValInfo;
import com.ForgeEssentials.util.OutputHandler;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TypeInfoMap extends TypeMultiValInfo
{
	public static final String	KEY	= "key";
	public static final String	VAL	= "value";

	public TypeInfoMap(ClassContainer container)
	{
		super(container);
	}

	@Override
	public void buildEntry(HashMap<String, ClassContainer> fields)
	{
		fields.put(KEY, new ClassContainer(container.getParameters()[0]));
		fields.put(VAL, new ClassContainer(container.getParameters()[1]));
	}

	@Override
	public Set<TypeData> getTypeDatasFromObject(Object obj)
	{
		HashSet<TypeData> datas = new HashSet<TypeData>();

		Set<Entry> list = ((Map) obj).entrySet();

		TypeData data;
		for (Entry e : list)
		{
			data = getEntryData();
			data.putField(KEY, e.getKey());
			data.putField(VAL, e.getValue());
			datas.add(data);
		}

		return datas;
	}

	@Override
	public String getEntryName()
	{
		return "Entry";
	}

	@Override
	public Object reconstruct(TypeData[] data, IReconstructData rawType)
	{
		Map map = new HashMap();
		try
		{
			map = (Map) container.getType().newInstance();
		}
		catch (Exception e)
		{
			OutputHandler.exception(Level.SEVERE, "Error instantiating " + container.getType().getCanonicalName() + "!", e);
			return null;
		}

		for (TypeData dat : data)
		{
			map.put(dat.getFieldValue(KEY), dat.getFieldValue(VAL));
		}

		return map;
	}

}
