package com.ForgeEssentials.api.data;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public abstract class TypeMultiValInfo implements ITypeInfo
{
	protected ClassContainer container;
	protected HashMap<String, Class> fields;
	private TypeEntryInfo entryInfo;

	public TypeMultiValInfo(ClassContainer container)
	{
		this.container = container;
		fields = new HashMap<String, Class>();
	}
	
	@Override
	public final void build()
	{
		build(fields);
		entryInfo = new TypeEntryInfo(fields);
	}
	
	/**
	 * the actual tyoes that this holds. An Entry class will be created for wach elemnt of this.
	 * @param fields
	 */
	public abstract void build(HashMap<String, Class> fields);

	@Override
	public boolean canSaveInline()
	{
		return false;
	}

	@Override
	public Class getTypeOfField(String field)
	{
		// will prolly never be called.
		return fields.get(field);
	}

	@Override
	public Class getType()
	{
		return container.getType();
	}

	@Override
	public Class[] getGenericTypes()
	{
		return container.getParameters();
	}

	@Override
	public String[] getFieldList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeData getTypeDataFromObject(Object obj)
	{
		Set<TypeData> datas = getTypeDatasFromObject(obj);
		TypeData data = DataStorageManager.getDataForType(container);
		int i = 0;
		for (TypeData dat : datas)
			data.putField("DataVal"+(i++), dat);
		return data;
	}
	
	public abstract Set<TypeData> getTypeDatasFromObject(Object obj);

	@Override
	public Object reconstruct(IReconstructData data)
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
