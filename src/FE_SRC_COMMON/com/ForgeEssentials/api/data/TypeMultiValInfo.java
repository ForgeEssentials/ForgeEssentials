package com.ForgeEssentials.api.data;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public abstract class TypeMultiValInfo<T> implements ITypeInfo<T>
{
	protected ClassContainer container;
	protected HashMap<String, Class> fields;

	public TypeMultiValInfo(ClassContainer container)
	{
		this.container = container;
	}
	
	@Override
	public final void build()
	{
		build(fields);
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
		// everything with this will be done from the EntryInfo
		return null;
	}

	@Override
	public Class<? extends T> getType()
	{
		return container.type;
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
	public TypeData getTypeDataFromObject(T obj)
	{
		Set<TypeData> datas = getTypeDatasFromObject();
		TypeData data = DataStorageManager.getDataForType(container);
		int i = 0;
		for (TypeData dat : datas)
			data.putField("arbitraty Num "+(i++), dat);
		return data;
	}
	
	public abstract Set<TypeData> getTypeDatasFromObject();

	@Override
	public T reconstruct(IReconstructData data)
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
	
	public abstract T reconstruct(TypeData[] data);
	
	public abstract TypeEntryInfo getEntryInfo();
}
