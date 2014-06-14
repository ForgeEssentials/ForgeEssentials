package com.forgeessentials.snooper.response;

import com.forgeessentials.api.json.JSONException;
import com.forgeessentials.api.json.JSONObject;
import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.core.compat.CompatMCStats;
import net.minecraftforge.common.Configuration;

import java.util.LinkedHashMap;

public class MCstatsInfo extends Response {
    LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();

    @Override
    public String getName()
    {
        return "CustomInfo";
    }

    @Override
    public void readConfig(String category, Configuration config)
    {
    }

    @Override
    public void writeConfig(String category, Configuration config)
    {
    }

    @Override
    public JSONObject getResponce(JSONObject input) throws JSONException
    {
        return new JSONObject().put(getName(), CompatMCStats.doSnooperStats());
    }

}
