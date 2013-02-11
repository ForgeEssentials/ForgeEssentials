package com.ForgeEssentials.api.data;

public interface IReconstructData
{
	public Object getFieldValue(String name);
	
	public String getUniqueKey();
	
	public Class getType();
}
