package com.ForgeEssentials.data.typeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.Set;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.api.data.TypeMultiValInfo;
import com.ForgeEssentials.util.OutputHandler;

public class TypeInfoMap extends TypeMultiValInfo
{
	public static final String	KEY			= "key";
	public static final String	VAL			= "value";

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

	@Override
	public Set<TypeData> getTypeDatasFromObject(Object obj)
	{
		HashSet<TypeData> datas = new HashSet<TypeData>();
		
		Set<Entry> list = ((Map)obj).entrySet();

		int i = 0;
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
	
	public String getEntryName()
	{
		return "Entry";
	}

	@Override
	public Object reconstruct(TypeData[] data)
	{
		Map map = new HashMap();
		try
		{
			map = (Map) container.getType().newInstance();
		}
		catch (Exception e)
		{
			OutputHandler.exception(Level.SEVERE, "Error instantiating "+container.getType().getCanonicalName()+"!", e);
			return null;
		}
		
		for (TypeData dat : data)
			map.put(dat.getFieldValue(KEY), dat.getFieldValue(VAL));
		
		
		return map;
	}

}
