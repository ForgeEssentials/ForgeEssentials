package com.forgeessentials.data.v2.types;

import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.Set;

import com.forgeessentials.data.v2.DataManager.DataType;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;

public class NBTTagCompoundType implements DataType<CompoundTag>
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
    public JsonElement serialize(CompoundTag src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        Set<String> tags = src.getAllKeys();
        for (String tagName : tags)
        {
            Tag tag = src.get(tagName);
            NumericTag tagPrimitive = (tag instanceof NumericTag) ? (NumericTag) tag : null;

            switch (tag.getId())
            {
            case EndTag.TAG_END:
                break;
            case EndTag.TAG_BYTE:
                result.add(JSON_BYTE + ":" + tagName, new JsonPrimitive(tagPrimitive.getAsByte()));
                break;
            case EndTag.TAG_SHORT:
                result.add(JSON_SHORT + ":" + tagName, new JsonPrimitive(tagPrimitive.getAsShort()));
                break;
            case EndTag.TAG_INT:
                result.add(JSON_INT + ":" + tagName, new JsonPrimitive(tagPrimitive.getAsInt()));
                break;
            case EndTag.TAG_LONG:
                result.add(JSON_LONG + ":" + tagName, new JsonPrimitive(tagPrimitive.getAsLong()));
                break;
            case EndTag.TAG_FLOAT:
                result.add(JSON_FLOAT + ":" + tagName, new JsonPrimitive(tagPrimitive.getAsFloat()));
                break;
            case EndTag.TAG_DOUBLE:
                result.add(JSON_DOUBLE + ":" + tagName, new JsonPrimitive(tagPrimitive.getAsDouble()));
                break;
            case EndTag.TAG_BYTE_ARRAY:
            {
                JsonArray jsonArray = new JsonArray();
                ByteArrayTag tagByteArray = (ByteArrayTag) tag;
                for (byte value : tagByteArray.getAsByteArray())
                {
                    jsonArray.add(new JsonPrimitive(value));
                }
                result.add(JSON_BYTE_ARRAY + ":" + tagName, jsonArray);
                break;
            }
            case EndTag.TAG_STRING:
                result.add(JSON_STRING + ":" + tagName, new JsonPrimitive(((StringTag) tag).getAsString()));
                break;
            case EndTag.TAG_LIST:
            {
                ListTag tagList = (ListTag) tag;
                JsonArray jsonArray = new JsonArray();
                String typeId;
                switch (tagList.getElementType())
                {
                case 0:
                    typeId = "S";
                    break;
                case EndTag.TAG_FLOAT:
                    typeId = "f";
                    for (int i = 0; i < tagList.size(); i++)
                        jsonArray.add(new JsonPrimitive(tagList.getFloat(i)));
                    break;
                case EndTag.TAG_DOUBLE:
                    typeId = "d";
                    for (int i = 0; i < tagList.size(); i++)
                        jsonArray.add(new JsonPrimitive(tagList.getDouble(i)));
                    break;
                case EndTag.TAG_STRING:
                    typeId = "S";
                    for (int i = 0; i < tagList.size(); i++)
                        jsonArray.add(context.serialize(tagList.getString(i)));
                    break;
                case EndTag.TAG_COMPOUND:
                    typeId = "c";
                    for (int i = 0; i < tagList.size(); i++)
                        jsonArray.add(context.serialize(tagList.getCompound(i)));
                    break;
                case EndTag.TAG_INT_ARRAY:
                    typeId = "i";
                    for (int i = 0; i < tagList.size(); i++)
                    {
                        JsonArray innerValues = new JsonArray();
                        int[] values = tagList.getIntArray(i);
                        for (int v : values)
                            innerValues.add(new JsonPrimitive(v));
                        jsonArray.add(innerValues);
                    }
                    break;
                default:
                    throw new RuntimeException(String.format("Unknown NBT data id %d", tagList.getElementType()));
                }
                result.add(typeId + ":" + tagName, jsonArray);
                break;
            }
            case EndTag.TAG_COMPOUND:
                result.add(JSON_COMPOUND + ":" + tagName, context.serialize(tag, CompoundTag.class));
                break;
            case EndTag.TAG_INT_ARRAY:
            {
                JsonArray jsonArray = new JsonArray();
                IntArrayTag tagIntArray = (IntArrayTag) tag;
                for (int value : tagIntArray.getAsIntArray())
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
    public CompoundTag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        try
        {
            JsonObject obj = json.getAsJsonObject();
            CompoundTag result = new CompoundTag();
            for (Entry<String, JsonElement> tagData : obj.entrySet())
            {
                char tagType = tagData.getKey().charAt(0);
                String tagName = tagData.getKey().substring(2, tagData.getKey().length());

                switch (tagType)
                {
                case JSON_BYTE:
                    result.putByte(tagName, (byte) context.deserialize(tagData.getValue(), Byte.class));
                    break;
                case JSON_BYTE_ARRAY:
                    if (tagData.getValue().isJsonArray())
                    {
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        byte[] byteArray = new byte[jsonArray.size()];
                        int index = 0;
                        for (JsonElement el : jsonArray)
                            byteArray[index++] = (byte) context.deserialize(el, Byte.class);
                        result.put(tagName, new ByteArrayTag(byteArray));
                    }
                    else
                    {
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_SHORT:
                    result.putShort(tagName, (short) context.deserialize(tagData.getValue(), Short.class));
                    break;
                case JSON_STRING:
                    if (tagData.getValue().isJsonArray())
                    {
                        ListTag tagList = new ListTag();
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        for (JsonElement el : jsonArray)
                        {
                            StringTag s = StringTag.valueOf(context.<String> deserialize(el, String.class));
                            tagList.add(s);
                        }
                        result.put(tagName, tagList);
                    }
                    else if (tagData.getValue().isJsonPrimitive())
                    {
                        result.putString(tagName, context.<String> deserialize(tagData.getValue(), String.class));
                    }
                    else
                    {
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_INT:
                    if (tagData.getValue().isJsonArray())
                    {
                        ListTag tagList = new ListTag();
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        for (JsonElement el : jsonArray)
                        {
                            IntTag s = IntTag.valueOf((int) context.deserialize(el, Integer.class));
                            tagList.add(s);
                        }
                        result.put(tagName, tagList);
                    }
                    else if (tagData.getValue().isJsonPrimitive())
                    {
                        result.putInt(tagName, (int) context.deserialize(tagData.getValue(), Integer.class));
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
                        result.put(tagName, new IntArrayTag(intArray));
                    }
                    else
                    {
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_FLOAT:
                    if (tagData.getValue().isJsonArray())
                    {
                        ListTag tagList = new ListTag();
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        for (JsonElement el : jsonArray)
                        {
                            FloatTag s = FloatTag.valueOf((float) context.deserialize(el, Float.class));
                            tagList.add(s);
                        }
                        result.put(tagName, tagList);
                    }
                    else if (tagData.getValue().isJsonPrimitive())
                    {
                        result.putFloat(tagName, (float) context.deserialize(tagData.getValue(), Float.class));
                    }
                    else
                    {
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_DOUBLE:
                    if (tagData.getValue().isJsonArray())
                    {
                        ListTag tagList = new ListTag();
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        for (JsonElement el : jsonArray)
                        {
                            DoubleTag s = DoubleTag.valueOf((double) context.deserialize(el, Double.class));
                            tagList.add(s);
                        }
                        result.put(tagName, tagList);
                    }
                    else if (tagData.getValue().isJsonPrimitive())
                    {
                        result.putDouble(tagName, (double) context.deserialize(tagData.getValue(), Double.class));
                    }
                    else
                    {
                        LoggingHandler.felog.error("Error parsing NBT data: Invalid data type");
                    }
                    break;
                case JSON_COMPOUND:
                    if (tagData.getValue().isJsonArray())
                    {
                        ListTag tagList = new ListTag();
                        JsonArray jsonArray = tagData.getValue().getAsJsonArray();
                        for (JsonElement el : jsonArray)
                        {
                            tagList.add((CompoundTag) context.deserialize(el, CompoundTag.class));
                        }
                        result.put(tagName, tagList);
                    }
                    else if (tagData.getValue().isJsonObject())
                    {
                        result.put(tagName, (CompoundTag) context.deserialize(tagData.getValue(), CompoundTag.class));
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
    public Class<CompoundTag> getType()
    {
        return CompoundTag.class;
    }

}
