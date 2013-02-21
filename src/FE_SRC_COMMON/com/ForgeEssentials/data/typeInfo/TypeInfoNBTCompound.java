package com.ForgeEssentials.data.typeInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.api.data.TypeMultiValInfo;

public class TypeInfoNBTCompound extends TypeMultiValInfo
{
	public static final String	KEY			= "name";
	public static final String	TYPE		= "type";
	public static final String	PRIMITIVE	= "value";
	public static final String	LIST		= "list";
	public static final String	COMPOUND	= "compound";

	public TypeInfoNBTCompound(ClassContainer container)
	{
		super(container);
	}

	@Override
	public void build(HashMap<String, Class> fields)
	{
		fields.put(KEY, String.class);
		//fields.put(VALUE, );
	}

	@Override
	public Set<TypeData> getTypeDatasFromObject(Object obj)
	{
		HashSet<TypeData> datas = new HashSet<TypeData>();
		
		NBTTagCompound nbt = (NBTTagCompound) obj;
		
		TypeData data;
		for (NBTBase tag : (Collection<NBTBase>) nbt.getTags())
		{
			data = new TypeData(new ClassContainer(tag.getClass()));
			data.putField(TYPE, tag.getId());
			data.putField(KEY, tag.getName());
			
			if (tag instanceof NBTTagCompound)
				data.putField(COMPOUND, tag);
			else if (tag instanceof NBTTagList)
				data.putField(LIST, tag);
			else
				data.putField(PRIMITIVE, tag);
			datas.add(data);
		}

		return datas;
	}

	@Override
	public Object reconstruct(TypeData[] data)
	{
		NBTTagCompound nbt = new NBTTagCompound();

		int type;
		String name;
		Object val;
		for (TypeData dat : data)
		{
			type = (Integer) dat.getFieldValue(TYPE);
			name = (String) dat.getFieldValue(KEY);

			switch (type)
				{
					case 1:
						nbt.setByte(name, (Byte) dat.getFieldValue(PRIMITIVE));
						break;
					case 2:
						nbt.setShort(name, (Short) dat.getFieldValue(PRIMITIVE));
						break;
					case 3:
						nbt.setInteger(name, (Integer) dat.getFieldValue(PRIMITIVE));
						break;
					case 4:
						nbt.setLong(name, (Long) dat.getFieldValue(PRIMITIVE));
						break;
					case 5:
						nbt.setFloat(name, (Float) dat.getFieldValue(PRIMITIVE));
						break;
					case 6:
						nbt.setDouble(name, (Double) dat.getFieldValue(PRIMITIVE));
						break;
					case 7:
						nbt.setByteArray(name, unboxArray((Byte[]) dat.getFieldValue(PRIMITIVE)));
						break;
					case 8:
						nbt.setString(name, (String) dat.getFieldValue(PRIMITIVE));
						break;
					case 9:
						nbt.setTag(name, (NBTTagList) dat.getFieldValue(LIST));
						break;
					case 10:
						nbt.setCompoundTag(name, (NBTTagCompound) dat.getFieldValue(COMPOUND));
						break;
					case 11:
						nbt.setIntArray(name, unboxArray((Integer[]) dat.getFieldValue(PRIMITIVE)));
						break;
				}
		}

		return nbt;
	}

	private static int[] unboxArray(Integer[] array)
	{
		int[] newArray = new int[array.length];

		for (int i = 0; i < array.length; i++)
			newArray[i] = array[i];

		return newArray;
	}

	private static byte[] unboxArray(Byte[] array)
	{
		byte[] newArray = new byte[array.length];

		for (int i = 0; i < array.length; i++)
			newArray[i] = array[i];

		return newArray;
	}

}
