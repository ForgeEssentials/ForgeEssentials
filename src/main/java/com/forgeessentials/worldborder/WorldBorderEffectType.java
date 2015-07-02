package com.forgeessentials.worldborder;

import java.lang.reflect.Type;

import com.forgeessentials.data.v2.DataManager.DataType;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.worldborder.effect.EffectMessage;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class WorldBorderEffectType implements DataType<WorldBorderEffect>
{

    private static final String TYPE = "type";

    @Override
    public JsonElement serialize(WorldBorderEffect src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject result = (JsonObject) context.serialize(src);
        result.add(TYPE, new JsonPrimitive(src.getClass().getSimpleName()));
        return result;
    }

    @Override
    public WorldBorderEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get(TYPE).getAsString();
        try
        {
            @SuppressWarnings("unchecked")
            Class<WorldBorderEffect> clazz = (Class<WorldBorderEffect>) Class.forName(EffectMessage.class.getPackage().getName() + "." + type);
            return context.deserialize(json, clazz);
        }
        catch (ClassNotFoundException e)
        {
            LoggingHandler.felog.error(String.format("Error parsing data: %s", json.toString()));
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Class<WorldBorderEffect> getType()
    {
        return WorldBorderEffect.class;
    }

}
