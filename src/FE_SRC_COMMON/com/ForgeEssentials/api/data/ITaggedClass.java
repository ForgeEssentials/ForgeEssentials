package com.ForgeEssentials.api.data;


public interface ITaggedClass
{
	public Object getFieldValue(String name);
	
	@Override
	public String toString();

	public Class getType();
}
