package com.ForgeEssentials.data.typeData;

import java.util.Map;

import com.ForgeEssentials.api.data.SaveableObject;

@SaveableObject
public class TypeDataOverrideMap<T extends Map> extends TypeDataOverrideMultiVal
{

	public TypeDataOverrideMap(Map map)
	{
		super(map);
	}

	@Override
	public String getTypeName()
	{
		return "JavaMap";
	}

	@Override
	public Object reconstruct()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void build()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class[] getSaveables()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
