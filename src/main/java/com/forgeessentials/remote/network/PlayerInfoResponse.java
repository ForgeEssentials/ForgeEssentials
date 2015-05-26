package com.forgeessentials.remote.network;

import java.util.Map;

import com.google.gson.JsonElement;

public class PlayerInfoResponse
{

    public String uuid;

    public String name;

    public Map<String, JsonElement> data;

    public PlayerInfoResponse(String uuid, String name)
    {
        this.uuid = uuid;
        this.name = name;
    }

}
