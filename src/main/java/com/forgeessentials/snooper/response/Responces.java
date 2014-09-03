package com.forgeessentials.snooper.response;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.snooper.ResponseRegistry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Responces extends Response {
    @Override
    public JsonElement getResponce(JsonObject input)
    {
        JsonArray data = new JsonArray();
        for (Response responce : ResponseRegistry.getAllResponses().values())
        {
            data.add(new JsonPrimitive(responce.id + " " + responce.getName()));
        }

        return data;
    }

    @Override
    public String getName()
    {
        return "Responces";
    }

    @Override
    public void readConfig(String category, Configuration config)
    {
    }

    @Override
    public void writeConfig(String category, Configuration config)
    {
    }
}
