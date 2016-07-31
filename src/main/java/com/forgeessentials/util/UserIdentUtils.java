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

/**
 * Created by alexa on 7/9/2016.
 */
public class UserIdentUtils
{
    public static UUID hexStringToUUID(String s)
    {
        if (s.length() != 32)
            throw new IllegalArgumentException();
        byte[] data = new byte[32];
        for (int i = 0; i < 32; i++)
        {
            data[i] = (byte) s.charAt(i);
        }
        return UUID.nameUUIDFromBytes(data);
    }

    public static UUID stringToUUID(String s)
    {
        if (s.length() == 32)
            return hexStringToUUID(s);
        else
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

    public static String fetchData(String url, String id)
    {
        try
        {
            LoggingHandler.felog.debug("Fetching " + id + " from " + url);
            URL uri = new URL(url);
            HttpsURLConnection huc = (HttpsURLConnection) uri.openConnection();
            InputStream is;
            JsonReader jr = new JsonReader(new InputStreamReader(is = huc.getInputStream()));
            if (is.available() > 0 && jr.hasNext())
            {
                jr.beginObject();
                String name = null;

                while (jr.hasNext())
                    if (jr.peek() == JsonToken.NAME)
                        name = jr.nextName();
                    else
                    {
                        if (jr.peek() == JsonToken.STRING)
                            if (name.equals(id))
                                return jr.nextString();
                        name = null;
                    }
                jr.endObject();
            }

            return null;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
