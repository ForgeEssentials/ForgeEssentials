package com.ForgeEssentials.data;

public class SavedField
{
	public String	name;
	public Object	value;
	public Class	type;

	public SavedField()
	{
	}

	public SavedField(String name, Object value)
	{
		this.name = name;
		this.value = value;
		type = value.getClass();
	}

	@Override
	public String toString()
	{
		return "{" + name + ", " + type + ", " + value + "}";
	}
}
