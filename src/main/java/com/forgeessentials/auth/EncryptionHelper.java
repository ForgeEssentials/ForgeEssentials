package com.forgeessentials.auth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

import com.google.common.base.Throwables;

public class EncryptionHelper
{

    private static final SecureRandom rand = new SecureRandom();

    protected static String algorithm;

    private static final String saltChars = "ABCDEFGHIJGMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-_=+[]{};:.,<>/?\\|~`";

    private static MessageDigest sha1;

    static
    {
        try
        {
            if (algorithm == null)
                algorithm = "SHA1"; // just in case
            sha1 = MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Hash a password. Should replicate PHP exactly.
     *
     * @param input
     * @return
     */
    public static String sha1(String input)
    {
        if (input == null)
            return null;
        try
        {
            byte[] array = input.getBytes("UTF-8");
            array = sha1.digest(array);
            return Hex.encodeHexString(array);
        }
        catch (UnsupportedEncodingException e)
        {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Hash password with salt. Should replicate PHP exactly.
     *
     * @param input
     * @return
     */
    public static String sha1(String input, String salt)
    {
        if (input == null)
            return null;
        else
            return sha1(input + salt);
    }

    public static String generateSalt()
    {
        return generateSalt(rand.nextInt(10) + 5);
    }

    public static String generateSalt(int length)
    {
        char[] array = new char[length];
        for (int i = 0; i < length; i++)
        {
            array[i] = saltChars.charAt(rand.nextInt(saltChars.length()));
        }
        return new String(array);
    }

}
