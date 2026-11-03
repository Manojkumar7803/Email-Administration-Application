package com.emailadmin;

import java.security.SecureRandom;

public final class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijkmnopqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String SYMBOLS = "@#$%&*!";

    private PasswordGenerator() {}

    public static String generate(int length) {
        if (length < 12) {
            throw new IllegalArgumentException("Password length must be at least 12.");
        }
        SecureRandom random = new SecureRandom();
        String all = UPPER + LOWER + DIGITS + SYMBOLS;
        StringBuilder result = new StringBuilder();
        result.append(randomChar(UPPER, random));
        result.append(randomChar(LOWER, random));
        result.append(randomChar(DIGITS, random));
        result.append(randomChar(SYMBOLS, random));
        while (result.length() < length) {
            result.append(randomChar(all, random));
        }
        // Shuffle so the required character types are not always at the beginning.
        char[] chars = result.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i]; chars[i] = chars[j]; chars[j] = temp;
        }
        return new String(chars);
    }

    private static char randomChar(String source, SecureRandom random) {
        return source.charAt(random.nextInt(source.length()));
    }
}
