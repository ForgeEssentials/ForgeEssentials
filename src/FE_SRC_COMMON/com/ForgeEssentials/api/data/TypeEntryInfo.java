package com.ForgeEssentials.api.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class TypeEntryInfo implements ITypeInfo
{
	private HashMap<String, Class> types;

	public TypeEntryInfo(HashMap<String, Class> types)
	{
		this.types = types;
	}

	@Override
	public boolean canSaveInline()
	{
		return true;
	}

	@Override
	public void build()
	{
		// unnecessary
	}

	@Override
	public Class getTypeOfField(String field)
	{
		// TODO Auto-generated method stub
		return types.get(field);
	}

	@Override
	public String[] getFieldList()
	{
		return types.keySet().toArray(new String[types.size()]);
	}

	@Override
	public TypeData getTypeDataFromObject(Object obj)
	{
		// no.. just no.
		// this will be handled internally by each MutiVal object
		return null;
	}

	@Override
	public Object reconstruct(IReconstructData data)
	{
		// do nothing.. this is a dummy class
		return data;
	}

	@Override
	public Class getType()
	{
		// why not? :)  better than void.class  .
		return Map.Entry.class;
	}

	@Override
	public Class[] getGenericTypes()
	{
		// prolly never will be used.
		return types.values().toArray(new Class[types.size()]);
	}

	@Override
	public ITypeInfo getInfoForField(String field)
	{
		return DataStorageManager.getInfoForType(getTypeOfField(field));
	}

}
