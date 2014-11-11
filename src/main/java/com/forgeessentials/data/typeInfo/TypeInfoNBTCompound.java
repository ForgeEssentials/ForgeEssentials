package com.forgeessentials.data.typeInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;

import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.data.api.TypeData;
import com.forgeessentials.data.api.TypeMultiValInfo;

public class TypeInfoNBTCompound extends TypeMultiValInfo {
    public static final String KEY = "name";
    public static final String TYPE = "type";
    public static final String PRIMITIVE = "value";
    public static final String TAG_LIST = "taglist";
    public static final String COMPOUND = "compound";
    public static final String B_ARRAY = "byteArray";
    public static final String I_ARRAY = "intArray";

    public TypeInfoNBTCompound(ClassContainer container)
    {
        super(container);
    }

    @Override
    public void buildEntry(HashMap<String, ClassContainer> fields)
    {
        fields.put(KEY, new ClassContainer(String.class));
        fields.put(TYPE, new ClassContainer(int.class));
        fields.put(PRIMITIVE, new ClassContainer(String.class));
        fields.put(TAG_LIST, new ClassContainer(NBTTagList.class));
        fields.put(COMPOUND, new ClassContainer(NBTTagCompound.class));
        fields.put(B_ARRAY, new ClassContainer(byte[].class));
        fields.put(I_ARRAY, new ClassContainer(int[].class));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<TypeData> getTypeDatasFromObject(Object obj)
    {
        HashSet<TypeData> datas = new HashSet<TypeData>();

        NBTTagCompound nbt = (NBTTagCompound) obj;

        TypeData data;
        NBTBase tag;
        for (String name : (Collection<String>) nbt.func_150296_c())
        {
            tag = nbt.getTag(name);
            data = getEntryData();
            data.putField(TYPE, tag.getId());
            data.putField(KEY, name);

            if (tag instanceof NBTTagCompound)
            {
                data.putField(COMPOUND, tag);
            }
            else if (tag instanceof NBTTagIntArray)
            {
                data.putField(I_ARRAY, ((NBTTagIntArray) tag).func_150302_c());
            }
            else if (tag instanceof NBTTagByteArray)
            {
                data.putField(B_ARRAY, ((NBTTagByteArray) tag).func_150292_c());
            }
            else if (tag instanceof NBTTagList)
            {
                data.putField(TAG_LIST, tag);
            }
            else if (tag instanceof NBTBase.NBTPrimitive)
            {
                String val = tag.toString();

                if (tag.getId() != new NBTTagInt(0).getId())
                {
                    val = val.substring(0, val.length() - 1);
                }

                data.putField(PRIMITIVE, val);
            }
            datas.add(data);
        }

        return datas;
    }

    @Override
    public ClassContainer getTypeOfField(String field)
    {
        if (field.equalsIgnoreCase(COMPOUND))
        {
            return new ClassContainer(NBTTagCompound.class);
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getEntryName()
    {
        return "compound";
    }

    @Override
    public Object reconstruct(TypeData[] data, IReconstructData rawType)
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
            {
                String v = (String) dat.getFieldValue(PRIMITIVE);
                if (v != null)
                    nbt.setString(name, v);
                break;
            }
            case 9:
                nbt.setTag(name, (NBTTagList) dat.getFieldValue(TAG_LIST));
                break;
            case 10:
                nbt.setTag(name, (NBTTagCompound) dat.getFieldValue(COMPOUND));
                break;
            case 11:
                nbt.setIntArray(name, (int[]) dat.getFieldValue(I_ARRAY));
                break;
            }
        }

        return nbt;
    }
}
