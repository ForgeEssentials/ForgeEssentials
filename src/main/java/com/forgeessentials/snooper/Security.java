package com.forgeessentials.snooper;

import com.forgeessentials.api.json.JSONArray;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Security {
    public static String encrypt(String input, String key)
    {
        try
        {
            byte[] crypted = null;
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
            return new String(Base64.encodeBase64String(crypted));
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return new JSONArray().put("wtf").toString();
        }
    }

    public static String decrypt(String input, String key)
    {
        try
        {
            byte[] output = null;
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decodeBase64(input));
            return new String(output);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return new JSONArray().put("KeyInvalid").toString();
        }
    }
}