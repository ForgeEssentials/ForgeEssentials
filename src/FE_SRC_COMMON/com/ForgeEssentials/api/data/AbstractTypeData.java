package com.ForgeEssentials.api.data;

import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

public abstract class AbstractTypeData implements IReconstructData
{
	private final Class type;
	private String uniqueKey;
	
	public AbstractTypeData(Class type)
	{
		this.type = type;
	}
	
	public abstract void addField(SavedField field);

	public abstract Set<SavedField> getAllFields();

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
		for (SavedField field : getAllFields())
			s.append(field.name).append("=").append(field.value).append(", ");
		s.replace(s.length() - 2, s.length(), "]");

		s.append("}");

		return s.toString();
	}

	public Class getType()
	{
		return type;
	}
}
