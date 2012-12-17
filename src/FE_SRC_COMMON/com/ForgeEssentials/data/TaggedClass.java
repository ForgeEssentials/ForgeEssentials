package com.ForgeEssentials.data;

import java.util.ArrayList;
import java.util.HashMap;

public class TaggedClass
{
	public class SavedField
	{
		public String FieldName;
		public Object Value;
		public Class Type;
		
		public SavedField() { }
		
		public SavedField(String name, Object value)
		{
			this.FieldName = name;
			this.Value = value;
			this.Type = value.getClass();
		}
	}
	
	public Class Type;
	public Object LoadingKey;
	public HashMap<String, SavedField> TaggedMembers;
	
	public TaggedClass()
	{
		this.TaggedMembers = new HashMap<String, SavedField>();
	}
	
	public void addField(SavedField field)
	{
		this.TaggedMembers.put(field.FieldName, field);
	}
}
