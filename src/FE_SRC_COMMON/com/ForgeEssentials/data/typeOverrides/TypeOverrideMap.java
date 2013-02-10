package com.ForgeEssentials.data.typeOverrides;

import java.util.Map;

import com.ForgeEssentials.api.data.SaveableObject;

@SaveableObject
public class TypeOverrideMap<T extends Map> extends TypeOverrideMultiVal
{

	public TypeOverrideMap(Map map)
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
