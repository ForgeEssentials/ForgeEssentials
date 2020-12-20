package com.forgeessentials.util;

import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class UserIdentUtils
{

    public static String reformatUUID(String s)
    {
        if (s.length() != 32)
            throw new IllegalArgumentException();
        return s.substring(0,8) + "-" + s.substring(8,12) + "-" + s.substring(12,16) + "-" + s.substring(16,20) + "-" + s.substring(20,32);
    }

    public static UUID stringToUUID(String s)
    {
        if (s.length() == 32)
            s = reformatUUID(s);
        return UUID.fromString(s);

    }

    public static UUID resolveMissingUUID(String name)
    {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        String data = fetchData(url, "id");
        if (data != null)
            return stringToUUID(data);
        return null;
    }

    public static String resolveMissingUsername(UUID id)
    {
        String url = "https://api.mojang.com/user/profiles/" + id.toString().replace("-", "") + "/names";
        return fetchData(url, "name");
    }

    private static String fetchData(String url, String id)
    {
        try
        {
            LoggingHandler.felog.debug("Fetching " + id + " from " + url);
            URL uri = new URL(url);
            HttpsURLConnection huc = (HttpsURLConnection) uri.openConnection();
            InputStream is;
            try {
                is = huc.getInputStream();
            } catch (IOException e) {
                is = huc.getErrorStream();
            }
            if (is == null) {
                return null;
            }
            //TODO: Rewrite so it won't become an infinite loop
            //      In the case of Resolving Username from UUID
            //      We need to account for multiple usernames
            //      And grab the current one
            try (JsonReader jr = new JsonReader(new InputStreamReader(is)))
            {
                boolean inArray = false;
                if (is.available() > 0 && jr.hasNext())
                {
                    if (jr.peek() == JsonToken.BEGIN_ARRAY)
                    {
                        inArray = true;
                        jr.beginArray();
                    }
                    JsonToken t = jr.peek();
                    String value = null;
                    while (t != JsonToken.END_ARRAY && t != JsonToken.END_DOCUMENT)
                    {
                        jr.beginObject();
                        String name = null;

                        while (jr.hasNext())
                        {
                            if (jr.peek() == JsonToken.NAME)
                                name = jr.nextName();
                            else
                            {
                                if (jr.peek() == JsonToken.STRING)
                                {
                                    if (id.equals(name))
                                    {
                                        value = jr.nextString();
                                    }
                                } else
                                {
                                    name = null;
                                }
                            }
                        }
                        jr.endObject();

                        t = jr.peek();
                    }
                    if (inArray)
                    {
                        jr.endArray();
                    }

                    if (value != null) {
                        return value;
                    }
                }
            }
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
