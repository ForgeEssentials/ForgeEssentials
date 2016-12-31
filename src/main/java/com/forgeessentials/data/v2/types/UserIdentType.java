package com.forgeessentials.data.v2.types;

import java.lang.reflect.Type;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.data.v2.DataManager.DataType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class UserIdentType implements DataType<UserIdent>
{

    @Override
    public JsonElement serialize(UserIdent src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.toSerializeString());
    }

    @Override
    public UserIdent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    {
        if (json.isJsonObject())
        {
            JsonObject obj = json.getAsJsonObject();
            JsonElement uuid = obj.get("uuid");
            JsonElement username = obj.get("username");
            if (uuid == null)
                return UserIdent.get(username.getAsString());
            else if (username == null)
                return UserIdent.get(uuid.getAsString());
            else
                return UserIdent.get(uuid.getAsString(), username.getAsString());
        }
        return UserIdent.fromString(json.getAsString());
    }

    @Override
    public Class<UserIdent> getType()
    {
        return UserIdent.class;
    }

}
