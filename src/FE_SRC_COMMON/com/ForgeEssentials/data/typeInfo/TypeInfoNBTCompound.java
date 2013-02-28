package com.ForgeEssentials.data.typeInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.api.data.TypeMultiValInfo;

public class TypeInfoNBTCompound extends TypeMultiValInfo
{
	public static final String	KEY			= "name";
	public static final String	TYPE		= "type";
	public static final String	PRIMITIVE	= "value";
	public static final String	COMPOUND	= "compound";
	public static final String	B_ARRAY		= "byteArray";
	public static final String	I_ARRAY		= "intArray";

	public TypeInfoNBTCompound(ClassContainer container)
	{
		super(container);
	}

	@Override
	public void build(HashMap<String, ClassContainer> fields)
	{
		fields.put(KEY, new ClassContainer(String.class));
		fields.put(TYPE, new ClassContainer(int.class));
		fields.put(PRIMITIVE, new ClassContainer(String.class));
		fields.put(COMPOUND, new ClassContainer(NBTTagCompound.class));
		fields.put(B_ARRAY, new ClassContainer(byte[].class));
		fields.put(I_ARRAY, new ClassContainer(int[].class));
	}

	@Override
	public Set<TypeData> getTypeDatasFromObject(Object obj)
	{
		HashSet<TypeData> datas = new HashSet<TypeData>();

		NBTTagCompound nbt = (NBTTagCompound) obj;

		TypeData data;
		for (NBTBase tag : (Collection<NBTBase>) nbt.getTags())
		{
			data = getEntryData();
			data.putField(TYPE, tag.getId());
			data.putField(KEY, tag.getName());

			if (tag instanceof NBTTagCompound)
			{
				data.putField(COMPOUND, tag);
			}
			else if (tag instanceof NBTTagIntArray)
			{
				data.putField(I_ARRAY, ((NBTTagIntArray) tag).intArray);
			}
			else if (tag instanceof NBTTagByteArray)
			{
				data.putField(B_ARRAY, ((NBTTagByteArray) tag).byteArray);
			}
			else
			{
				String val = null;
				switch (tag.getId())
					{
						case 1:
							val = "" + nbt.getByte(tag.getName());
							break;
						case 2:
							val = "" + nbt.getShort(tag.getName());
							break;
						case 3:
							val = "" + nbt.getInteger(tag.getName());
							break;
						case 4:
							val = "" + nbt.getLong(tag.getName());
							break;
						case 5:
							val = "" + nbt.getFloat(tag.getName());
							break;
						case 6:
							val = "" + nbt.getDouble(tag.getName());
							break;
						case 8:
							val = "" + nbt.getString(tag.getName());
							break;
					}

				data.putField(PRIMITIVE, val);
			}
			datas.add(data);
		}

		return datas;
	}

	@Override
	public String getEntryName()
	{
		return "NBTTag";
	}

	@Override
	public Object reconstruct(TypeData[] data)
	{
		NBTTagCompound nbt = new NBTTagCompound();

		int type;
		String name;
		for (TypeData dat : data)
		{
			type = (Integer) dat.getFieldValue(TYPE);
			name = (String) dat.getFieldValue(KEY);

			switch (type)
				{
					case 1:
						nbt.setByte(name, Byte.parseByte(dat.getFieldValue(PRIMITIVE).toString()));
						break;
					case 2:
						nbt.setShort(name, Short.parseShort(dat.getFieldValue(PRIMITIVE).toString()));
						break;
					case 3:
						nbt.setInteger(name, Integer.parseInt(dat.getFieldValue(PRIMITIVE).toString()));
						break;
					case 4:
						nbt.setLong(name, Long.parseLong(dat.getFieldValue(PRIMITIVE).toString()));
						break;
					case 5:
						nbt.setFloat(name, Float.parseFloat(dat.getFieldValue(PRIMITIVE).toString()));
						break;
					case 6:
						nbt.setDouble(name, Double.parseDouble(dat.getFieldValue(PRIMITIVE).toString()));
						break;
					case 7:
						nbt.setByteArray(name, (byte[]) dat.getFieldValue(B_ARRAY));
						break;
					case 8:
						nbt.setString(name, (String) dat.getFieldValue(PRIMITIVE));
						break;
					case 10:
						nbt.setCompoundTag(name, (NBTTagCompound) dat.getFieldValue(COMPOUND));
						break;
					case 11:
						nbt.setIntArray(name, (int[]) dat.getFieldValue(I_ARRAY));
						break;
				}
		}

		return nbt;
	}
}
