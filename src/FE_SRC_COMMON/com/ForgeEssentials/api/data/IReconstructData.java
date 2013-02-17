package com.ForgeEssentials.api.data;

import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

public interface IReconstructData
{
	public Object getFieldValue(String name);
	
	public String getUniqueKey();
	
	public Class getType();
	
	public Collection getAllValues();
}
