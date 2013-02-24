package com.ForgeEssentials.data.typeInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.api.data.TypeMultiValInfo;

public class TypeInfoArray extends TypeMultiValInfo
{
	public static final String	POS		= "ElementPos";
	public static final String	ELEMENT	= "Element";

	public TypeInfoArray(ClassContainer container)
	{
		super(container);
	}

	@Override
	public void build(HashMap<String, ClassContainer> fields)
	{
		fields.put(POS, new ClassContainer(int.class));
		fields.put(ELEMENT, new ClassContainer(container.getType().getComponentType()));
	}

	@Override
	public Set<TypeData> getTypeDatasFromObject(Object obj)
	{
		HashSet<TypeData> datas = new HashSet<TypeData>();
		
		Object[] array = (Object[]) obj;

		int i = 0;
		TypeData data;
		for (Object element : array)
		{
			data = getEntryData();
			data.putField(POS, i);
			data.putField(ELEMENT, element);
			datas.add(data);
		}

		return datas;
	}

	@Override
	public Object reconstruct(TypeData[] data)
	{
		Object array = Array.newInstance(container.getType().getComponentType(), data.length);

		for (TypeData dat : data)
		{
			Array.set(array, (Integer) dat.getFieldValue(POS), dat.getFieldValue(ELEMENT));
		}

		return array;
	}
}
