package com.ForgeEssentials.api.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import com.ForgeEssentials.data.SavedField;

public class TypeData implements IReconstructData, Serializable
{
	private final Class type;
	private String uniqueKey;
	private HashMap<String, Object>	members;

	public TypeData(Class type)
	{
		this.type = type;
		members = new HashMap<String, Object>();
	}
	
	public void putField(String name, Object value)
	{
		SavedField field = new SavedField(name, value);
		members.put(name, field);
	}

	public Object getFieldValue(String name)
	{
		return members.get(name);
	}
	
	public boolean hasField(String field)
	{
		return members.keySet().contains(field);
	}
	
	public Set<Entry<String, Object>> getAllFields()
	{
		return members.entrySet();
	}

	public void setUniqueKey(String key)
	{
		uniqueKey = key;
	}

	public String getUniqueKey()
	{
		return uniqueKey;
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("{");
		s.append("type=").append(getType()).append(", ");
		s.append("unique=").append(uniqueKey).append(", ");

		s.append("[");
		for (Entry<String, Object> field : getAllFields())
			s.append(field.getKey()).append("=").append(field.getValue()).append(", ");
		s.replace(s.length() - 2, s.length(), "]");

		s.append("}");

		return s.toString();
	}

	public Class getType()
	{
		return type;
	}
}
