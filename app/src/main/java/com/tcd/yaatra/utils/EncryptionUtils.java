package com.tcd.yaatra.utils;

import android.util.Log;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    private static final String TAG = "EncryptionUtils";
    private static SecretKey secretKey;
    private static final String password = "nyi$478H%Osdj@67";

    static byte[] key = password.getBytes();
    static SecureRandom secureRandom;

    static {
        secureRandom = new SecureRandom();
        secretKey = new SecretKeySpec(key, "AES");
    }

    public static String encrypt(String plainText) {
        try {
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherBytes = cipher.doFinal(plainText.getBytes());

            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherBytes.length);
            byteBuffer.putInt(iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherBytes);
            byte[] cipherMessage = byteBuffer.array();

            String encryptedText = Base64.getEncoder().encodeToString(cipherMessage);

            //return encryptedText;

            return plainText;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to encrypt text", e);
        } catch (BadPaddingException e) {
            Log.e(TAG, "Failed to encrypt text", e);
        } catch (NoSuchPaddingException e) {
            Log.e(TAG, "Failed to encrypt text", e);
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Failed to encrypt text", e);
        } catch (IllegalBlockSizeException e) {
            Log.e(TAG, "Failed to encrypt text", e);
        } catch (InvalidAlgorithmParameterException e) {
            Log.e(TAG, "Failed to encrypt text", e);
        }

        return plainText;
    }

    public static String decrypt(String encryptedText) {

        try {

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);

            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedBytes);
            int ivLength = byteBuffer.getInt();
            byte[] iv = new byte[ivLength];
            byteBuffer.get(iv);
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, iv));
            byte[] plainTextBytes = cipher.doFinal(cipherText);

            //return new String(plainTextBytes);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to decrypt text", e);
        } catch (BadPaddingException e) {
            Log.e(TAG, "Failed to decrypt text", e);
        } catch (NoSuchPaddingException e) {
            Log.e(TAG, "Failed to decrypt text", e);
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Failed to decrypt text", e);
        } catch (IllegalBlockSizeException e) {
            Log.e(TAG, "Failed to decrypt text", e);
        } catch (InvalidAlgorithmParameterException e) {
            Log.e(TAG, "Failed to decrypt text", e);
        }

        return encryptedText;
    }
}
