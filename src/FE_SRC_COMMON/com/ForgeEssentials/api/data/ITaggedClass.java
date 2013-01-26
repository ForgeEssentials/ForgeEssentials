package com.ForgeEssentials.api.data;

import java.util.HashMap;
import java.util.Map.Entry;

public interface ITaggedClass
{
	public Object getFieldValue(String name);
	
	@Override
	public String toString();

	public Class getType();
}
