package com.forgeessentials.serverNetwork.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    public static String encryptString(String input, String privateKey) throws Exception {
        // Convert the private key to bytes
        byte[] privateKeyBytes = privateKey.getBytes(StandardCharsets.UTF_8);

        // Create a secret key from the private key bytes
        SecretKey secretKey = new SecretKeySpec(privateKeyBytes, "AES");

        // Initialize the cipher with the secret key and encryption mode
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Encrypt the input string
        byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

        // Encode the encrypted bytes as Base64
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

        // Return the encrypted string
        return encryptedBase64;
    }

    public static String decryptString(String encryptedInput, String privateKey) throws Exception {
        // Decode the private key from Base64
        byte[] privateKeyBytes = privateKey.getBytes(StandardCharsets.UTF_8);

        // Create a secret key from the private key bytes
        SecretKey secretKey = new SecretKeySpec(privateKeyBytes, "AES");

        // Initialize the cipher with the secret key and decryption mode
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Decode the encrypted input from Base64
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedInput);

        // Decrypt the encrypted bytes
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // Convert the decrypted bytes to a string
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Generates a new random passkey
     */
    public static String generatePasskey(int length)
    {
        StringBuilder passkey = new StringBuilder();
        Random rnd;
        try
        {
            rnd = SecureRandom.getInstanceStrong();
        }
        catch (NoSuchAlgorithmException e)
        {
            rnd = new SecureRandom();
        }
        for (int i = 0; i < length; i++)
            passkey.append(StringCleaning.PASSKEY_CHARS[rnd.nextInt(StringCleaning.PASSKEY_CHARS.length)]);
        return passkey.toString();
    }
    /**
     * Generates a new random privateKey
     */
    public static String generatePrivateKey()
    {
        try
        {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128); /* 128-bit AES */
            SecretKey secret = gen.generateKey();
            byte[] binary = secret.getEncoded();
            String text = String.format("%032X", new BigInteger(+1, binary));
            return text;
        }
        catch (NoSuchAlgorithmException e)
        {
            return new String("TheBestSecretKey".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        }
    }
}
