package com.forgeessentials.data.v2.types;

import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.forgeessentials.data.v2.DataManager.DataType;
import com.forgeessentials.util.OutputHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class NBTTagCompoundType implements DataType<NBTTagCompound> {

    //@SuppressWarnings({ "unchecked"})
    @Override
    public JsonElement serialize(NBTTagCompound src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        @SuppressWarnings("unchecked")
        Set<String> tags = src.func_150296_c();
        for (String tagName : tags)
        {
            NBTBase tag = src.getTag(tagName);
            NBTPrimitive tagPrimitive = (tag instanceof NBTPrimitive) ? (NBTPrimitive) tag : null;
            switch (tag.getId())
            {
            case 1:
                // NBTTagByte
                result.add("b:" + tagName, new JsonPrimitive(tagPrimitive.func_150290_f()));
                break;
            case 2:
                // NBTTagShort
                result.add("s:" + tagName, new JsonPrimitive(tagPrimitive.func_150289_e()));
                break;
            case 3:
                // NBTTagInt
                result.add("i:" + tagName, new JsonPrimitive(tagPrimitive.func_150287_d()));
                break;
            case 4:
                // NBTTagLong
                result.add("l:" + tagName, new JsonPrimitive(tagPrimitive.func_150291_c()));
                break;
            case 5:
                // NBTTagFloat
                result.add("f:" + tagName, new JsonPrimitive(tagPrimitive.func_150288_h()));
                break;
            case 6:
                // NBTTagDouble
                result.add("d:" + tagName, new JsonPrimitive(tagPrimitive.func_150286_g()));
                break;
            case 7:
            {
                JsonArray jsonArray = new JsonArray();
                NBTTagByteArray tagByteArray = (NBTTagByteArray) tag;
                for (byte value : tagByteArray.func_150292_c())
                {
                    jsonArray.add(new JsonPrimitive(value));
                }
                result.add("B:" + tagName, jsonArray);
                break;
            }
            case 8:
                // NBTTagString
                result.add("S:" + tagName, new JsonPrimitive(((NBTTagString) tag).func_150285_a_()));
                break;
            case 9:
            {
                NBTTagList tagList = (NBTTagList) tag;
                JsonArray jsonArray = new JsonArray();
                String typeId = "-";
                switch (tagList.func_150303_d())
                {
                case 5:
                    typeId = "f";
                    for (int i = 0; i < tagList.tagCount(); i++)
                        jsonArray.add(new JsonPrimitive(tagList.func_150308_e(i)));
                    break;
                case 6:
                    typeId = "d";
                    for (int i = 0; i < tagList.tagCount(); i++)
                        jsonArray.add(new JsonPrimitive(tagList.func_150309_d(i)));
                    break;
                case 10:
                    typeId = "c";
                    for (int i = 0; i < tagList.tagCount(); i++)
                        jsonArray.add(context.serialize(tagList.getCompoundTagAt(i)));
                    break;
                case 11:
                    typeId = "i";
                    for (int i = 0; i < tagList.tagCount(); i++)
                    {
                        JsonArray innerValues = new JsonArray();
                        int[] values = tagList.func_150306_c(i);
                        for (int v : values)
                            innerValues.add(new JsonPrimitive(v));
                        jsonArray.add(innerValues);
                    }
                    break;
                default:
                    throw new RuntimeException();
                }
                result.add(typeId + ":" + tagName, jsonArray);
                break;
            }
            case 10:
                result.add("c:" + tagName, context.serialize(tag, NBTTagCompound.class));
                break;
            case 11:
            {
                JsonArray jsonArray = new JsonArray();
                NBTTagIntArray tagByteArray = (NBTTagIntArray) tag;
                for (int value : tagByteArray.func_150302_c())
                {
                    jsonArray.add(new JsonPrimitive(value));
                }
                result.add("I:" + tagName, jsonArray);
                break;
            }
            default:
                throw new RuntimeException();
            }
        }
        return result;
    }

    @Override
    public NBTTagCompound deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        try
        {
            JsonObject obj = json.getAsJsonObject();
            NBTTagCompound result = new NBTTagCompound();
            for (Entry<String, JsonElement> tagData : obj.entrySet())
            {
                char tagType = tagData.getKey().charAt(0);
                String tagName = tagData.getKey().substring(2, tagData.getKey().length());
    
                switch (tagType)
                {
                case 'b':
                    result.setByte(tagName, (byte) context.deserialize(tagData.getValue(), Byte.class));
                    break;
                case 'B':
                    if (tagData.getValue().isJsonArray())
                    {
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        byte[] byteArray = new byte[jsonArray.size()];
                        int index = 0;
                        for (JsonElement el : jsonArray)
                            byteArray[index++] = (byte) context.deserialize(el, Byte.class);
                        result.setTag(tagName, new NBTTagByteArray(byteArray));
                    }
                    else
                    {
                        OutputHandler.felog.severe("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case 's':
                    result.setShort(tagName, (short) context.deserialize(tagData.getValue(), Short.class));
                    break;
                case 'i':
                    if (tagData.getValue().isJsonArray())
                    {
                        NBTTagList tagList = new NBTTagList();
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        for (JsonElement el : jsonArray)
                        {
                            tagList.appendTag(new NBTTagInt((int) context.deserialize(el, Integer.class)));
                        }
                        result.setTag(tagName, tagList);
                    }
                    else if (tagData.getValue().isJsonPrimitive())
                    {
                        result.setInteger(tagName, (int) context.deserialize(tagData.getValue(), Integer.class));
                    }
                    else
                    {
                        OutputHandler.felog.severe("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case 'I':
                    if (tagData.getValue().isJsonArray())
                    {
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        int[] intArray = new int[jsonArray.size()];
                        int index = 0;
                        for (JsonElement el : jsonArray)
                            intArray[index++] = (int) context.deserialize(el, Integer.class);
                        result.setTag(tagName, new NBTTagIntArray(intArray));
                    }
                    else
                    {
                        OutputHandler.felog.severe("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case 'f':
                    if (tagData.getValue().isJsonArray())
                    {
                        NBTTagList tagList = new NBTTagList();
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        for (JsonElement el : jsonArray)
                        {
                            tagList.appendTag(new NBTTagFloat((float) context.deserialize(el, Float.class)));
                        }
                        result.setTag(tagName, tagList);
                    }
                    else if (tagData.getValue().isJsonPrimitive())
                    {
                        result.setFloat(tagName, (float) context.deserialize(tagData.getValue(), Float.class));
                    }
                    else
                    {
                        OutputHandler.felog.severe("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case 'd':
                    if (tagData.getValue().isJsonArray())
                    {
                        NBTTagList tagList = new NBTTagList();
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        for (JsonElement el : jsonArray)
                        {
                            tagList.appendTag(new NBTTagDouble((double) context.deserialize(el, Double.class)));
                        }
                        result.setTag(tagName, tagList);
                    }
                    else if (tagData.getValue().isJsonPrimitive())
                    {
                        result.setDouble(tagName, (double) context.deserialize(tagData.getValue(), Double.class));
                    }
                    else
                    {
                        OutputHandler.felog.severe("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case 'c':
                    if (tagData.getValue().isJsonArray())
                    {
                        NBTTagList tagList = new NBTTagList();
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        for (JsonElement el : jsonArray)
                        {
                            tagList.appendTag((NBTTagCompound) context.deserialize(el, NBTTagCompound.class));
                        }
                        result.setTag(tagName, tagList);
                    }
                    else if (tagData.getValue().isJsonObject())
                    {
                        result.setTag(tagName, (NBTTagCompound) context.deserialize(tagData.getValue(), NBTTagCompound.class));
                    }
                    else
                    {
                        OutputHandler.felog.severe("Error parsing NBT data: Invalid data type");
                    }
                    break;
                default:
                    OutputHandler.felog.severe("Error parsing NBT data: Invalid data type");
                    break;
                }
            }
            return result;
        }
        catch (Throwable e)
        {
            OutputHandler.felog.severe(String.format("Error parsing data: %s", json.toString()));
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Class<NBTTagCompound> getType()
    {
        return NBTTagCompound.class;
    }

}
