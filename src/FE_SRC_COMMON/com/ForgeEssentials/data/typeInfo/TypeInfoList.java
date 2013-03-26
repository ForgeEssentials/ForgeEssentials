package com.ForgeEssentials.data.typeInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.api.data.TypeMultiValInfo;
import com.ForgeEssentials.util.OutputHandler;

public class TypeInfoList extends TypeMultiValInfo
{
	public static final String	POS		= "ElementPos";
	public static final String	ELEMENT	= "Element";

	public TypeInfoList(ClassContainer container)
	{
		super(container);
	}

	@Override
	public void build(HashMap<String, ClassContainer> fields)
	{
		fields.put(POS, new ClassContainer(int.class));
		fields.put(ELEMENT, new ClassContainer(container.getParameters()[0]));
	}

	@Override
	public Set<TypeData> getTypeDatasFromObject(Object obj)
	{
		HashSet<TypeData> datas = new HashSet<TypeData>();

		List<?> list = (List<?>) obj;

		TypeData data;
		for (int i = 0; i < list.size(); i++)
		{
			data = getEntryData();
			data.putField(POS, i);
			data.putField(ELEMENT, list.get(i));
			datas.add(data);
		}

		return datas;
	}

	@Override
	public Object reconstruct(TypeData[] data)
	{
		Object array = Array.newInstance(container.getType(), data.length);

		for (TypeData dat : data)
		{
			Array.set(array, (Integer) dat.getFieldValue(POS), dat.getFieldValue(ELEMENT));
		}

		List<Object> list = new ArrayList<Object>(data.length);
		try
		{
			list = (List<Object>) container.getType().newInstance();
		}
		catch (Exception e)
		{
			OutputHandler.exception(Level.SEVERE, "Error instantiating " + container.getType().getCanonicalName() + "!", e);
			return null;
		}

		for (int i = 0; i < data.length; i++)
		{
			list.add(Array.get(array, i));
		}

		return list;
	}

}
