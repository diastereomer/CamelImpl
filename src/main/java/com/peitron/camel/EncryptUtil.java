package com.peitron.camel;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

public class EncryptUtil {

    public static String ENCRYPT_KEY = "Encrypt";

    private static String rawKey = "secret";

    private static SecretKey key = null;

    private static SecureRandom secureRandom = new SecureRandom();

    private static String CHARSET = "UTF-8";

    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static SecretKey getSecretKey() throws Exception {
        if (key == null) {
            byte[] rawKeyData = rawKey.getBytes(CHARSET);
            DESKeySpec dks = new DESKeySpec(rawKeyData);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            key = keyFactory.generateSecret(dks);
        }
        return key;
    }

    public static String encrypt(String input) throws Exception {
        if (input == null || input.length() == 0) {
            return input;
        }

        SecretKey key = getSecretKey();
        Cipher c = Cipher.getInstance("DES");
        c.init(Cipher.ENCRYPT_MODE, key, secureRandom);
        byte[] cipherByte = c.doFinal(input.getBytes(CHARSET));
        String dec = new String(encodeHex(cipherByte));
        return dec;
    }

    public static String decrypt(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }
        byte[] dec = decodeHex(input.toCharArray());
        try {
            SecretKey key = getSecretKey();
            Cipher c = Cipher.getInstance("DES");
            c.init(Cipher.DECRYPT_MODE, key, secureRandom);
            byte[] clearByte = c.doFinal(dec);
            return new String(clearByte, CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static char[] encodeHex(byte[] data) {
        int len = data.length;
        char[] out = new char[len << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < len; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return out;
    }

    public static byte[] decodeHex(char[] data) {

        int len = data.length;

        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    private static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }
}

