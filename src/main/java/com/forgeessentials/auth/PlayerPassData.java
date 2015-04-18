package com.forgeessentials.auth;

import java.util.HashMap;
import java.util.UUID;

import com.forgeessentials.data.v2.DataManager;

public class PlayerPassData {

    private static HashMap<UUID, PlayerPassData> cache = new HashMap<UUID, PlayerPassData>();
    
    public final String password;

    public PlayerPassData(String password)
    {
        this.password = password;
    }

    /**
     * Checks, if a player is registered
     * 
     * @param userID
     * @return
     */
    public static boolean isRegistered(UUID userID)
    {
        return getPassword(userID) != null;
    }

    /**
     * Checks the password
     *
     * @param userID
     * @param plainPassword
     * @return success
     */
    public static boolean checkPassword(UUID userID, String plainPassword)
    {
        return ModuleAuth.encrypt(plainPassword).equals(getPassword(userID));
    }

    /**
     * Returns the PlayerPassData if it exists.
     *
     * @param userID
     * @return encoded password
     */
    private static String getPassword(UUID userID)
    {
        PlayerPassData data = cache.get(userID);
        if (data == null)
            data = DataManager.getInstance().load(PlayerPassData.class, userID.toString());
        return data == null ? null : data.password;
    }

    /**
     * Creates a PlayerPassData
     *
     * @param userID
     * @param plainPassword
     */
    public static void setPassword(UUID userID, String plainPassword)
    {
        if (plainPassword == null)
        {
            DataManager.getInstance().delete(PlayerPassData.class, userID.toString());
            cache.remove(userID);
        }
        else
        {
            PlayerPassData data = new PlayerPassData(ModuleAuth.encrypt(plainPassword));
            DataManager.getInstance().save(data, userID.toString());
            cache.put(userID, data);
        }
    }

    public static void removeFromCache(UUID userID)
    {
        cache.remove(userID);
    }

}
