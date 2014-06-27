/**
 * Just a renamed version of ServerTools' AccountUtils, with a few things added
 */
package com.forgeessentials.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

public class PlayerUtils {

    private static final Gson gson = new Gson();

    private static final Cache<String, String> usernameToUUID = CacheBuilder.newBuilder().build();
    private static final Cache<String, String> UUIDToUsername = CacheBuilder.newBuilder().build();

    private PlayerUtils()
    {
    }

    /**
     * Get a player's username given their UUID
     * The username is cached for future use
     *
     * @param uuid the player's UUID
     * @return the player's username
     */
    public static String getUsername(final String uuid)
    {

        String username = "";

        try
        {
            username = UUIDToUsername.get(uuid, new Callable<String>() {
                @Override
                public String call() throws Exception
                {

                    URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + URLEncoder.encode(uuid.replace("-", ""), "UTF-8"));
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())))
                    {
                        Account account = gson.fromJson(reader, Account.class);
                        return account.name;
                    }
                }
            });
        }
        catch (Exception e)
        {
            OutputHandler.felog.getWrapper().warn("Failed to fetch username from UUID", e);
        }

        return username;
    }

    /**
     * Get a player's UUID from their username
     * The UUID is cached for future use
     *
     * @param username the player's username
     * @return the player's UUID
     */
    public static String getUUID(final String username)
    {

        String uuid = "";

        try
        {
            uuid = usernameToUUID.get(username, new Callable<String>() {
                @Override
                public String call() throws Exception
                {

                    URL url = new URL("https://api.mojang.com/profiles/minecraft");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");

                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream()))
                    {
                        writer.write(gson.toJson(username).getBytes());
                    }

                    Account[] account;

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
                    {
                        account = gson.fromJson(reader, Account[].class);
                    }

                    return (account != null && account.length > 0) ? account[0].id : "";
                }
            });
        }
        catch (Exception e)
        {

            OutputHandler.felog.getWrapper().warn("Failed to fetch UUID  from username", e);
        }

        uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"); // Add the dashes back into the UUID

        return uuid;
    }

    public static class Account {

        public String id;
        public String name;

        @Override
        public String toString()
        {
            return "Account{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
        }
    }
}
