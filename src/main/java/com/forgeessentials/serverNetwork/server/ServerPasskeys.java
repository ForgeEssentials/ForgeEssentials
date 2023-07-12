//package com.forgeessentials.serverNetwork.server;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.util.HashMap;
//import java.util.Random;
//
//import org.apache.commons.codec.binary.Hex;
//
//import com.forgeessentials.api.UserIdent;
//import com.forgeessentials.data.v2.DataManager;
//import com.forgeessentials.serverNetwork.ModuleNetworking;
//
//public class ServerPasskeys extends HashMap<UserIdent, String>
//{
//    private static final long serialVersionUID = -8268113844466668789L; /* default */
//
//    /**
//     * Generates a new random passkey
//     */
//    public String generatePasskey()
//    {
//        StringBuilder passkey = new StringBuilder();
//        Random rnd;
//        try
//        {
//            rnd = SecureRandom.getInstanceStrong();
//        }
//        catch (NoSuchAlgorithmException e)
//        {
//            rnd = new SecureRandom();
//        }
//        for (int i = 0; i < ModuleNetworking.passkeyLength; i++)
//            passkey.append(ModuleNetworking.PASSKEY_CHARS[rnd.nextInt(ModuleNetworking.PASSKEY_CHARS.length)]);
//        return passkey.toString();
//    }
//
//    /**
//     * Get stored passkey for user or generate a new one and save it
//     * 
//     * @param userIdent
//     */
//    public String getPasskey(UserIdent userIdent)
//    {
//        if (containsKey(userIdent))
//            return get(userIdent);
//        String passkey = generatePasskey();
//        setPasskey(userIdent, passkey);
//        return passkey;
//    }
//
//    /**
//     * Set and save a new passkey for a user
//     * 
//     * @param userIdent
//     * @param passkey
//     */
//    public void setPasskey(UserIdent userIdent, String passkey)
//    {
//        if (passkey == null)
//            remove(userIdent);
//        else
//        {
//            // TODO: Think about hashes passkeys
//            // passkey = hashPasskey(passkey);
//            put(userIdent, passkey);
//        }
//        DataManager.save(this, ModuleNetworking.getSaveFile());
//    }
//
//    public static String hashPasskey(String passkey)
//    {
//        try
//        {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(passkey.getBytes());
//            passkey = Hex.encodeHexString(md.digest());
//        }
//        catch (NoSuchAlgorithmException e)
//        {
//            /* do nothing */
//        }
//        return passkey;
//    }
//}
