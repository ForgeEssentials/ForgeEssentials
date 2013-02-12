package com.ForgeEssentials.data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.ForgeEssentials.api.data.AbstractTypeData;

public class TypeData extends AbstractTypeData
{
	private HashMap<String, Object>	members;

	protected TypeData(Class c)
	{
		super(c);
		members = new HashMap<String, Object>();
	}
	
	@Override
	public void putField(String name, Object value)
	{
		SavedField field = new SavedField(name, value);
		members.put(name, field);
	}

	@Override
	public Object getFieldValue(String name)
	{
		return members.get(name);
	}
	
	@Override
	public Set<Entry<String, Object>> getAllFields()
	{
		return members.entrySet();
	}
}
