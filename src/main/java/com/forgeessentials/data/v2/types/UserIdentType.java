package com.forgeessentials.data.v2.types;

import java.lang.reflect.Type;

import com.forgeessentials.data.v2.DataManager.DataType;
import com.forgeessentials.util.UserIdent;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class UserIdentType implements DataType<UserIdent> {

    @Override
    public JsonElement serialize(UserIdent src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public UserIdent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    {
        if (json.isJsonObject())
            return new UserIdent(json.getAsJsonObject().get("uuid").getAsString(), json.getAsJsonObject().get("username").getAsString());
        return UserIdent.fromString(json.getAsString());
    }

    @Override
    public Class<UserIdent> getType()
    {
        return UserIdent.class;
    }

}
