package com.forgeessentials.snooper;

import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.util.OutputHandler;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResponseRegistry {
    private static List<Response> map = new ArrayList<>();

    /**
     * Register a response for an ID. Use the API!
     *
     * @param ID
     * @param response
     */
    public static void registerResponse(Integer ID, Response response)
    {
        if (map.get(ID) != null)
        {
            throw new RuntimeException("You are attempting to register a response on an used ID: " + ID);
        }
        else
        {
            OutputHandler.felog.finer("Response " + response.getName() + " ID: " + ID + " registered!");
            response.id = ID;
            map.add(ID, response);
        }
    }

    /**
     * Used to build the response.
     *
     * @param ID
     * @return
     */
    public static Response getResponse(int ID)
    {
        if (map.get(ID) != null)
        {
            return map.get(ID);
        }
        else
        {
            return map.get(0);
        }
    }

    /**
     * Used by config
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Collection<Response> getAllresponses()
    {
        return ImmutableList.copyOf(map);
    }
}
