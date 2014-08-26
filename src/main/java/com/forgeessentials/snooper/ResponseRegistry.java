package com.forgeessentials.snooper;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;

import scala.actors.threadpool.Arrays;

import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.util.OutputHandler;

public class ResponseRegistry {
    private static TIntObjectHashMap<Response> map = new TIntObjectHashMap<Response>();

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
    public static Collection<Response> getAllresponses()
    {
        return Arrays.asList(map.values());
    }
}
