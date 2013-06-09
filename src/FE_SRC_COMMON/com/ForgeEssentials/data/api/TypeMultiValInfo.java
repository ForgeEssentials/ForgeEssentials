package com.ForgeEssentials.data.api;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.data.StorageManager;

public abstract class TypeMultiValInfo implements ITypeInfo
{
	protected ClassContainer				container;
	private HashMap<String, ClassContainer>	entryFields;
	private HashMap<String, ClassContainer>	fields;
	private TypeEntryInfo					entryInfo;

	public static final String				UID	= "_$EntryID$_";

	public TypeMultiValInfo(ClassContainer container)
	{
		this.container = container;
		fields = new HashMap<String, ClassContainer>();
		entryFields = new HashMap<String, ClassContainer>();
		entryFields.put(UID, new ClassContainer(String.class));
	}

	@Override
	public final void build()
	{
		build(fields);
		buildEntry(entryFields);
		entryInfo = new TypeEntryInfo(entryFields, container);
	}

	/**
	 * Fields that the elements of this MultiVal opbject should have.
	 * @param entryFields
	 */
	public abstract void buildEntry(HashMap<String, ClassContainer> entryFields);

	public void build(HashMap<String, ClassContainer> entryFields)
	{
		// optional override
	}

	@Override
	public boolean canSaveInline()
	{
		return false;
	}

	@Override
	public ClassContainer getTypeOfField(String field)
	{
		if (field == null)
			return null;

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

	public String[] getEntryFieldList()
	{
		return entryFields.keySet().toArray(new String[entryFields.size()]);
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

		addExtraDataForObject(data, obj);

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

	public void addExtraDataForObject(TypeData data, Object obj)
	{
		// optional override
	}

	@Override
	public final Object reconstruct(IReconstructData data)
	{
		Collection values = data.getAllValues();
		ArrayList<TypeData> list = new ArrayList();
		for (Object obj : values)
		{
			if (obj instanceof TypeData)
			{
				list.add((TypeData) obj);
			}
		}
		TypeData[] datas = list.toArray(new TypeData[list.size()]);
		return reconstruct(datas, data);
	}

	public abstract Object reconstruct(TypeData[] data, IReconstructData rawData);

	@Override
	public final ITypeInfo getInfoForField(String field)
	{
		if (field.toLowerCase().contains(getEntryName().toLowerCase()))
			return getEntryInfo();
		else
			return DataStorageManager.getInfoForType(getTypeOfField(field));
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
