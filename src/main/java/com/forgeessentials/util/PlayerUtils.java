package com.forgeessentials.util;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerUtils {

    private static final Gson gson = new Gson();

    private static final Map<String, String> map = new HashMap<String, String>();

    public static UUID fromPlayerName(String playername)
    {
        String uuid = map.get(playername);

        if (uuid == null)
        {

        }
        return null;

    }
}
