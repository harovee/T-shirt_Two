package com.shop.server.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class SecurityUtil {

    private static final String ALGORITHM_NAME = "PBKDF2WithHmacSHA1";
    private static final Integer ITERATION_COUNT = 65536;
    private static final Integer KEY_LENGTH= 128;

    public static String[] hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM_NAME);
        byte[] hash = f.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();
        return new String[]{enc.encodeToString(salt), enc.encodeToString(hash)};
    }

    public static boolean verifyPassword(String password, String salt, String hash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Base64.Decoder dec = Base64.getDecoder();
        byte[] saltBytes = dec.decode(salt);
        byte[] hashBytes = dec.decode(hash);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM_NAME);
        byte[] testHash = f.generateSecret(spec).getEncoded();
        return Arrays.equals(hashBytes, testHash);
    }

    public static String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

}