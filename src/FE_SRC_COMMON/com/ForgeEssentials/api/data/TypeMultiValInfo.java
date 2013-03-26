package com.ForgeEssentials.api.data;

import java.rmi.server.UID;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ForgeEssentials.data.StorageManager;

public abstract class TypeMultiValInfo implements ITypeInfo
{
	protected ClassContainer					container;
	protected HashMap<String, ClassContainer>	fields;
	private TypeEntryInfo						entryInfo;

	public static final String					UID	= "_$EntryID$_";

	public TypeMultiValInfo(ClassContainer container)
	{
		this.container = container;
		fields = new HashMap<String, ClassContainer>();
		fields.put(UID, new ClassContainer(String.class));
	}

	@Override
	public final void build()
	{
		build(fields);
		entryInfo = new TypeEntryInfo(fields, container);
	}

	/**
	 * the actual types that this holds. An Entry class will be created for which element of this.
	 * @param fields
	 */
	public abstract void build(HashMap<String, ClassContainer> fields);

	@Override
	public boolean canSaveInline()
	{
		return false;
	}

	@Override
	public ClassContainer getTypeOfField(String field)
	{
		if (field.toLowerCase().contains(getEntryName().toLowerCase()))
			return entryInfo.getType();
		return fields.get(field);
	}

	@Override
	public ClassContainer getType()
	{
		return container;
	}

	@Override
	public Class[] getGenericTypes()
	{
		return container.getParameters();
	}

	@Override
	public String[] getFieldList()
	{
		return fields.keySet().toArray(new String[fields.size()]);
	}

	@Override
	public final TypeData getTypeDataFromObject(Object obj)
	{
		Set<TypeData> datas = getTypeDatasFromObject(obj);
		TypeData data = DataStorageManager.getDataForType(container);

		ITypeInfo entry = getEntryInfo();
		ITypeInfo tempInfo;

		String id = new UID().toString();
		String unique = container.getFileSafeName() + id;

		int i = 0;
		for (TypeData dat : datas)
		{
			for (Entry<String, Object> e : dat.getAllFields())
			{
				if (e.getValue() != null && !(e.getValue() instanceof TypeData) && StorageManager.isTypeComplex(e.getValue().getClass()))
				{
					tempInfo = entry.getInfoForField(e.getKey());
					dat.putField(e.getKey(), DataStorageManager.getDataForObject(tempInfo.getType(), e.getValue()));
				}
			}

			dat.putField(UID, id);
			data.putField(getEntryName() + i++, dat);
		}
		data.setUniqueKey(unique);
		return data;
	}

	public String getEntryName()
	{
		return "DataVal";
	}

	public static String getUIDFromUnique(String unique)
	{
		return unique.substring(unique.lastIndexOf('_'));
	}

	public abstract Set<TypeData> getTypeDatasFromObject(Object obj);

	@Override
	public final Object reconstruct(IReconstructData data)
	{
		Collection values = data.getAllValues();
		TypeData[] datas = new TypeData[values.size()];
		int i = 0;
		for (Object obj : values)
		{
			datas[i++] = (TypeData) obj;
		}
		return reconstruct(datas);
	}

	public abstract Object reconstruct(TypeData[] data);

	@Override
	public final ITypeInfo getInfoForField(String field)
	{
		return getEntryInfo();
	}

	public TypeEntryInfo getEntryInfo()
	{
		return entryInfo;
	}

	protected TypeData getEntryData()
	{
		return new TypeData(new ClassContainer(Map.Entry.class, container.parameters));
	}
}
