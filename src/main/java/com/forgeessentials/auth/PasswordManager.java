package com.forgeessentials.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.forgeessentials.data.v2.DataManager;

public class PasswordManager
{

    private static String salt;

    public static void setSalt(String salt)
    {
        PasswordManager.salt = salt;
    }

    /**
     * Data class to store encrypted passwords
     */
    public static class PlayerPassData
    {

        private String password;

        public List<UUID> sessions;

        public PlayerPassData(String password)
        {
            this.password = password;
            sessions = new ArrayList<>();
        }

        public boolean checkPassword(String plainPassword)
        {
            return PasswordManager.encrypt(plainPassword).equals(password);
        }

        public void setEncryptedPassword(String password)
        {
            this.password = password;
        }

    }

    /**
     * Set and save user password
     *
     * @param user
     * @param plainPassword
     */
    public static void setPassword(UUID user, String plainPassword)
    {
        if (plainPassword == null)
        {
            DataManager.getInstance().delete(PlayerPassData.class, user.toString());
        }
        else
        {
            PlayerPassData password = new PlayerPassData(PasswordManager.encrypt(plainPassword));
            DataManager.getInstance().save(password, user.toString());
        }
    }

    /**
     * Verify password
     *
     * @param user
     * @param plainPassword
     * @return success
     */
    public static boolean checkPassword(UUID user, String plainPassword)
    {
        PlayerPassData password = getPassword(user);
        if (password == null)
            return false;
        return password.checkPassword(plainPassword);
    }

    /**
     * Checks if the player is registered
     *
     * @param user
     * @return
     */
    public static boolean hasPassword(UUID user)
    {
        return DataManager.getInstance().exists(PlayerPassData.class, user.toString());
    }

    /**
     * Returns the PlayerPassData if it exists.
     *
     * @param user
     * @return encoded password
     */
    private static PlayerPassData getPassword(UUID user)
    {
        return DataManager.getInstance().load(PlayerPassData.class, user.toString());
    }

    /**
     * Encrypt a password for use with authentication module (not really a good encryption, but enough for just a game
     * 
     * @param str
     * @return
     */
    public static String encrypt(String str)
    {
        return EncryptionHelper.sha1(str, salt);
    }

    /**
     * Checks if a player has an autologin session
     * @param user
     * @param session
     * @return
     */

    public static boolean hasSession(UUID user, UUID session)
    {
        PlayerPassData pass = getPassword(user);
        if (pass == null)
            return false;
        return pass.sessions.contains(session);
    }

    public static void addSession(UUID user, UUID session)
    {
        PlayerPassData pass = getPassword(user);
        pass.sessions.add(session);
        DataManager.getInstance().save(pass, user.toString());
    }

    public static void invalidateAllSessions(UUID user)
    {
        PlayerPassData pass = getPassword(user);
        pass.sessions.clear();
        DataManager.getInstance().save(pass, user.toString());
    }

}
