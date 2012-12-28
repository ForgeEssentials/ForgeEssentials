package com.ForgeEssentials.data;

import java.util.HashMap;

public class TaggedClass
{
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
			this.type = value.getClass();
		}
	}

	public Class						type;
	protected SavedField					uniqueKey;
	protected HashMap<String, SavedField>	TaggedMembers;

	public TaggedClass()
	{
		this.TaggedMembers = new HashMap<String, SavedField>();
	}

	public void addField(SavedField field)
	{
		this.TaggedMembers.put(field.name, field);
	}

	public Object getFieldValue(String name)
	{
		if (!uniqueKey.name.endsWith("()") && uniqueKey.name.equals(name))
			return uniqueKey.value;
		else
			return TaggedMembers.get(name);
	}
}
