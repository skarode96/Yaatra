package com.tcd.yaatra.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtils {

    private static final String password = "ksi*ns$PS3266@DgdE";
    private static SecretKey secretKey;

    static{
        secretKey = new SecretKeySpec(password.getBytes(), "AES");
    }

    public static String encrypt(String plainText)
             {

        return plainText;

       /* Cipher cipher = null;
        cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherBytes = cipher.doFinal(plainText.getBytes());

        String encryptedText = Base64.getEncoder().encodeToString(cipherBytes);

        return encryptedText;*/
    }

    public static String decrypt(String encryptedText)
             {

        return encryptedText;

        /*Cipher cipher = null;
        cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);

        byte[] plainTextBytes = cipher.doFinal(decodedBytes);

        return Base64.getEncoder().encodeToString(plainTextBytes);*/
    }
}
