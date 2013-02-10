package com.ForgeEssentials.data.typeOverrides;

import java.util.Map;

import com.ForgeEssentials.api.data.SaveableObject;

@SaveableObject
public class TypeOverrideMap<T extends Map> extends TypeOverride
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

}
