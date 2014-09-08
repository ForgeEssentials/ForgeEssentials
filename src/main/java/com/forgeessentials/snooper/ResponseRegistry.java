package com.forgeessentials.snooper;

import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.util.OutputHandler;
import com.google.common.collect.ImmutableMap;

public class ResponseRegistry {
	
    private static Map<Integer, Response> map = new HashMap<>();

    /**
     * Register a response for an ID. Use the API!
     *
     * @param ID
     * @param response
     */
    public static void registerResponse(Integer ID, Response response)
    {
        if (map.containsKey(ID))
        {
            throw new RuntimeException("You are attempting to register a response on an used ID: " + ID);
        }
        else
        {
            OutputHandler.felog.finer("Response " + response.getName() + " ID: " + ID + " registered!");
            response.id = ID;
            map.put(ID, response);
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
        if (map.containsKey(ID))
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
    public static ImmutableMap<Integer, Response> getAllResponses()
    {
    	return ImmutableMap.copyOf(map);
    }
}
