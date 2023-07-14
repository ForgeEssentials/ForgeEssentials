package com.forgeessentials.serverNetwork.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    private static final String ENCRYPTION_ALGORITHM = "AES-256";

    public static String encryptString(String input, String privateKey) throws Exception {
        // Convert the private key to bytes
        byte[] privateKeyBytes = privateKey.getBytes(StandardCharsets.UTF_8);

        // Create a secret key from the private key bytes
        Key secretKey = new SecretKeySpec(privateKeyBytes, ENCRYPTION_ALGORITHM);

        // Initialize the cipher with the secret key and encryption mode
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Encrypt the input string
        byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

        // Convert the encrypted bytes to a string
        return new String(encryptedBytes, StandardCharsets.UTF_8);
    }

    public static String decryptString(String encryptedInput, String privateKey) throws Exception {
        // Convert the private key to bytes
        byte[] privateKeyBytes = privateKey.getBytes(StandardCharsets.UTF_8);

        // Create a secret key from the private key bytes
        Key secretKey = new SecretKeySpec(privateKeyBytes, ENCRYPTION_ALGORITHM);

        // Initialize the cipher with the secret key and decryption mode
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Decrypt the encrypted string
        byte[] decryptedBytes = cipher.doFinal(encryptedInput.getBytes(StandardCharsets.UTF_8));

        // Convert the decrypted bytes to a string
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
