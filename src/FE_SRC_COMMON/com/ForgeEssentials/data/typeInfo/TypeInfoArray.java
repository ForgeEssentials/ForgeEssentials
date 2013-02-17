package com.ForgeEssentials.data.typeInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ForgeEssentials.api.data.ClassContainer;
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
	public void build(HashMap<String, Class> fields)
	{
		fields.put(POS, byte.class);
		fields.put(ELEMENT, container.type.getComponentType());
	}

	@Override
	public Set<TypeData> getTypeDatasFromObject(Object obj)
	{
		HashSet<TypeData> datas = new HashSet<TypeData>();

		List list = new ArrayList();

		if (obj instanceof Object[])
		{
			list = Arrays.asList((Object[]) obj);
		}
		else if (obj instanceof byte[])
		{
			for (byte i : (byte[]) obj)
			{
				list.add(i);
			}
		}
		else if (obj instanceof short[])
		{
			for (short i : (short[]) obj)
			{
				list.add(i);
			}
		}
		else if (obj instanceof int[])
		{
			for (int i : (int[]) obj)
			{
				list.add(i);
			}
		}
		else if (obj instanceof float[])
		{
			for (float i : (float[]) obj)
			{
				list.add(i);
			}
		}
		else if (obj instanceof double[])
		{
			for (double i : (double[]) obj)
			{
				list.add(i);
			}
		}
		else if (obj instanceof char[])
		{
			for (char i : (char[]) obj)
			{
				list.add(i);
			}
		}
		else if (obj instanceof long[])
		{
			for (long i : (long[]) obj)
			{
				list.add(i);
			}
		}
		else if (obj instanceof boolean[])
		{
			for (boolean i : (boolean[]) obj)
			{
				list.add(i);
			}
		}

		int i = 0;
		TypeData data;
		for (Object element : list)
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
		Object array = Array.newInstance(getType(), data.length);

		for (TypeData dat : data)
		{
			Array.set(array, (Integer) dat.getFieldValue(POS), dat.getFieldValue(ELEMENT));
		}

		return array;
	}
}
