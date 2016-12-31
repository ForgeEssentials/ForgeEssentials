package com.forgeessentials.playerlogger.remote.serializer;

import java.lang.reflect.Type;

import com.forgeessentials.data.v2.DataManager.DataType;
import com.forgeessentials.playerlogger.entity.WorldData;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class WorldDataType implements DataType<WorldData>
{

    @Override
    public JsonElement serialize(WorldData src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.id);
    }

    @Override
    public WorldData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    {
        WorldData wd = new WorldData();
        wd.id = json.getAsInt();
        return wd;
    }

    @Override
    public Class<WorldData> getType()
    {
        return WorldData.class;
    }

}
