package com.forgeessentials.playerlogger.remote.serializer;

import java.lang.reflect.Type;

import com.forgeessentials.data.v2.DataManager.DataType;
import com.forgeessentials.playerlogger.entity.PlayerData;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class PlayerDataType implements DataType<PlayerData>
{

    @Override
    public JsonElement serialize(PlayerData src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.id);
    }

    @Override
    public PlayerData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    {
        PlayerData wd = new PlayerData();
        wd.id = json.getAsLong();
        return wd;
    }

    @Override
    public Class<PlayerData> getType()
    {
        return PlayerData.class;
    }

}
