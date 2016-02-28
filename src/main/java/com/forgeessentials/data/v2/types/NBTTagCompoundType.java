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
import net.minecraftforge.common.util.Constants.NBT;

import com.forgeessentials.data.v2.DataManager.DataType;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class NBTTagCompoundType implements DataType<NBTTagCompound>
{

    public static final char JSON_BYTE = 'b';
    public static final char JSON_SHORT = 's';
    public static final char JSON_INT = 'i';
    public static final char JSON_LONG = 'l';
    public static final char JSON_FLOAT = 'f';
    public static final char JSON_DOUBLE = 'd';
    public static final char JSON_BYTE_ARRAY = 'B';
    public static final char JSON_STRING = 'S';
    public static final char JSON_COMPOUND = 'c';
    public static final char JSON_INT_ARRAY = 'I';

    // @SuppressWarnings({ "unchecked"})
    @Override
    public JsonElement serialize(NBTTagCompound src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        @SuppressWarnings("unchecked")
        Set<String> tags = src.getKeySet();
        for (String tagName : tags)
        {
            NBTBase tag = src.getTag(tagName);
            NBTPrimitive tagPrimitive = (tag instanceof NBTPrimitive) ? (NBTPrimitive) tag : null;

            switch (tag.getId())
            {
            case NBT.TAG_END:
                break;
            case NBT.TAG_BYTE:
                result.add(JSON_BYTE + ":" + tagName, new JsonPrimitive(tagPrimitive.getByte()));
                break;
            case NBT.TAG_SHORT:
                result.add(JSON_SHORT + ":" + tagName, new JsonPrimitive(tagPrimitive.getShort()));
                break;
            case NBT.TAG_INT:
                result.add(JSON_INT + ":" + tagName, new JsonPrimitive(tagPrimitive.getInt()));
                break;
            case NBT.TAG_LONG:
                result.add(JSON_LONG + ":" + tagName, new JsonPrimitive(tagPrimitive.getLong()));
                break;
            case NBT.TAG_FLOAT:
                result.add(JSON_FLOAT + ":" + tagName, new JsonPrimitive(tagPrimitive.getFloat()));
                break;
            case NBT.TAG_DOUBLE:
                result.add(JSON_DOUBLE + ":" + tagName, new JsonPrimitive(tagPrimitive.getDouble()));
                break;
            case NBT.TAG_BYTE_ARRAY:
            {
                JsonArray jsonArray = new JsonArray();
                NBTTagByteArray tagByteArray = (NBTTagByteArray) tag;
                for (byte value : tagByteArray.getByteArray())
                {
                    jsonArray.add(new JsonPrimitive(value));
                }
                result.add(JSON_BYTE_ARRAY + ":" + tagName, jsonArray);
                break;
            }
            case NBT.TAG_STRING:
                result.add(JSON_STRING + ":" + tagName, new JsonPrimitive(((NBTTagString) tag).getString()));
                break;
            case NBT.TAG_LIST:
            {
                NBTTagList tagList = (NBTTagList) tag;
                JsonArray jsonArray = new JsonArray();
                String typeId;
                switch (tagList.getTagType())
                {
                case NBT.TAG_FLOAT:
                    typeId = "f";
                    for (int i = 0; i < tagList.tagCount(); i++)
                        jsonArray.add(new JsonPrimitive(tagList.getFloat(i)));
                    break;
                case NBT.TAG_DOUBLE:
                    typeId = "d";
                    for (int i = 0; i < tagList.tagCount(); i++)
                        jsonArray.add(new JsonPrimitive(tagList.getDouble(i)));
                    break;
                case NBT.TAG_STRING:
                    typeId = "S";
                    for (int i = 0; i < tagList.tagCount(); i++)
                        jsonArray.add(context.serialize(tagList.getStringTagAt(i)));
                    break;
                case NBT.TAG_COMPOUND:
                    typeId = "c";
                    for (int i = 0; i < tagList.tagCount(); i++)
                        jsonArray.add(context.serialize(tagList.getCompoundTagAt(i)));
                    break;
                case NBT.TAG_INT_ARRAY:
                    typeId = "i";
                    for (int i = 0; i < tagList.tagCount(); i++)
                    {
                        JsonArray innerValues = new JsonArray();
                        int[] values = tagList.getIntArray(i);
                        for (int v : values)
                            innerValues.add(new JsonPrimitive(v));
                        jsonArray.add(innerValues);
                    }
                    break;
                default:
                    throw new RuntimeException(String.format("Unknown NBT data id %d", tagList.getTagType()));
                }
                result.add(typeId + ":" + tagName, jsonArray);
                break;
            }
            case NBT.TAG_COMPOUND:
                result.add(JSON_COMPOUND + ":" + tagName, context.serialize(tag, NBTTagCompound.class));
                break;
            case NBT.TAG_INT_ARRAY:
            {
                JsonArray jsonArray = new JsonArray();
                NBTTagIntArray tagIntArray = (NBTTagIntArray) tag;
                for (int value : tagIntArray.getIntArray())
                {
                    jsonArray.add(new JsonPrimitive(value));
                }
                result.add(JSON_INT_ARRAY + ":" + tagName, jsonArray);
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
                case JSON_BYTE:
                    result.setByte(tagName, (byte) context.deserialize(tagData.getValue(), Byte.class));
                    break;
                case JSON_BYTE_ARRAY:
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
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_SHORT:
                    result.setShort(tagName, (short) context.deserialize(tagData.getValue(), Short.class));
                    break;
                case JSON_STRING:
                    if (tagData.getValue().isJsonArray())
                    {
                        NBTTagList tagList = new NBTTagList();
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        for (JsonElement el : jsonArray)
                        {
                            tagList.appendTag(new NBTTagString(context.<String> deserialize(el, String.class)));
                        }
                        result.setTag(tagName, tagList);
                    }
                    else if (tagData.getValue().isJsonPrimitive())
                    {
                        result.setString(tagName, context.<String> deserialize(tagData.getValue(), String.class));
                    }
                    else
                    {
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_INT:
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
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_INT_ARRAY:
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
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_FLOAT:
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
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_DOUBLE:
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
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_COMPOUND:
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
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                default:
                    LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    break;
                }
            }
            return result;
        }
        catch (Throwable e)
        {
            LoggingHandler.felog.error(String.format("Error parsing data: %s", json.toString()));
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
