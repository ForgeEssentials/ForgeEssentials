package com.ForgeEssentials.api.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

public abstract class AbstractTypeData implements IReconstructData, Serializable
{
	private final Class type;
	private String uniqueKey;
	
	protected AbstractTypeData(Class type)
	{
		this.type = type;
	}
	
	public abstract void putField(String name, Object value);

	public abstract Set<Entry<String, Object>> getAllFields();

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
