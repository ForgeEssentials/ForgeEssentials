package com.forgeessentials.data.typeInfo;

import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.TypeData;
import com.forgeessentials.data.api.TypeMultiValInfo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class TypeInfoNBTTagList extends TypeMultiValInfo {
    public static final String KEY = "name";
    public static final String TYPE = "type";
    public static final String PRIMITIVE = "value";
    public static final String TAG_LIST = "TAG_LIST";
    public static final String COMPOUND = "compound";
    public static final String B_ARRAY = "byteArray";
    public static final String I_ARRAY = "intArray";

    public TypeInfoNBTTagList(ClassContainer container)
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

        NBTTagList nbt = (NBTTagList) obj;

        int count = nbt.tagCount();
        int type = nbt.func_150303_d();

        TypeData data;
        for (int i = 0;i<count;++i)
        {
            data = getEntryData();
            data.putField(TYPE,type);

            switch(type){
                case 5 :
                    data.putField(PRIMITIVE, String.valueOf(nbt.func_150308_e(i)));
                    break;
                case 6 :
                    data.putField(PRIMITIVE, String.valueOf(nbt.func_150309_d(i)));
                    break;
                case 8 :
                    data.putField(PRIMITIVE, nbt.getStringTagAt(i));
                    break;
                case 10:
                    data.putField(COMPOUND, nbt.getCompoundTagAt(i));
                    break;
                case 11:
                    NBTTagIntArray intArray = new NBTTagIntArray(nbt.func_150306_c(i));
                    data.putField(I_ARRAY, intArray);
                    break;
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
        NBTTagList nbt = new NBTTagList();

        int type;
        for (TypeData dat : data)
        {
            type = (Integer) dat.getFieldValue(TYPE);
            switch (type)
            {
            case 1:
                NBTTagByte nbtByte = new NBTTagByte(Byte.parseByte(dat.getFieldValue(PRIMITIVE).toString()));
                nbt.appendTag(nbtByte);
                break;
            case 2:
                NBTTagShort nbtShort = new NBTTagShort(Short.parseShort(dat.getFieldValue(PRIMITIVE).toString()));
                nbt.appendTag(nbtShort);
                break;
            case 3:
                NBTTagInt nbtInt = new NBTTagInt(Integer.parseInt(dat.getFieldValue(PRIMITIVE).toString()));
                nbt.appendTag(nbtInt);
                break;
            case 4:
                NBTTagLong nbtLong = new NBTTagLong(Long.parseLong(dat.getFieldValue(PRIMITIVE).toString()));
                nbt.appendTag(nbtLong);
                break;
            case 5:
                NBTTagFloat nbtFloat = new NBTTagFloat(Float.parseFloat(dat.getFieldValue(PRIMITIVE).toString()));
                nbt.appendTag(nbtFloat);
                break;
            case 6:
                NBTTagDouble nbtDouble = new NBTTagDouble(Double.parseDouble(dat.getFieldValue(PRIMITIVE).toString()));
                nbt.appendTag(nbtDouble);
                break;
            case 7:
                NBTTagByteArray nbtByteArray = new NBTTagByteArray((byte[]) dat.getFieldValue(B_ARRAY));
                nbt.appendTag(nbtByteArray);
                break;
            case 8:
                NBTTagString nbtString = new NBTTagString((String) dat.getFieldValue(PRIMITIVE));
                nbt.appendTag(nbtString);
                break;
            case 9:
                NBTTagList nbtTagList = (NBTTagList) dat.getFieldValue(TAG_LIST);
                nbt.appendTag(nbtTagList);
                break;
            case 10:
                NBTTagCompound nbtTagCompound = (NBTTagCompound) dat.getFieldValue(COMPOUND);
                nbt.appendTag(nbtTagCompound);
                break;
            case 11:
                NBTTagIntArray nbtIntArray = new NBTTagIntArray((int[]) dat.getFieldValue(I_ARRAY));
                nbt.appendTag(nbtIntArray);
                break;
            }
        }

        return nbt;
    }
}