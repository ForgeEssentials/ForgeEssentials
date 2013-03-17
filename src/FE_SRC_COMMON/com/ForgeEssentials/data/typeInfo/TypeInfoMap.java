package com.ForgeEssentials.data.typeInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.api.data.TypeMultiValInfo;
import com.ForgeEssentials.util.OutputHandler;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TypeInfoMap extends TypeMultiValInfo
{
	public static final String	KEY	= "key";
	public static final String	VAL	= "value";

	public TypeInfoMap(ClassContainer container)
	{
		super(container);
	}

	@Override
	public void build(HashMap<String, ClassContainer> fields)
	{
		fields.put(KEY, new ClassContainer(container.getParameters()[0]));
		fields.put(VAL, new ClassContainer(container.getParameters()[1]));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Set<TypeData> getTypeDatasFromObject(Object obj)
	{
		HashSet<TypeData> datas = new HashSet<TypeData>();

		Set<Entry> list = ((Map) obj).entrySet();

		TypeData data;
		for (Entry<?, ?> e : list)
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object reconstruct(TypeData[] data)
	{
		Map<Object, Object> map = new HashMap<Object, Object>();
		try
		{
			map = (Map<Object, Object>) container.getType().newInstance();
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
