package com.forgeessentials.playerlogger.remote.serializer;

import java.lang.reflect.Type;

import com.forgeessentials.data.v2.DataManager.DataType;
import com.forgeessentials.playerlogger.entity.BlockData;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class BlockDataType implements DataType<BlockData>
{

    @Override
    public JsonElement serialize(BlockData src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.id);
    }

    @Override
    public BlockData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    {
        BlockData wd = new BlockData();
        wd.id = json.getAsInt();
        return wd;
    }

    @Override
    public Class<BlockData> getType()
    {
        return BlockData.class;
    }

}
