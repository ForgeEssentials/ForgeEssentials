package com.ForgeEssentials.data;

import java.util.ArrayList;

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
	public SavedField LoadingKey;
	public ArrayList<SavedField> TaggedMembers;
	
	public TaggedClass()
	{
		this.TaggedMembers = new ArrayList<SavedField>();
	}
	
	public void addField(SavedField field)
	{
		this.TaggedMembers.add(field);
	}
}
