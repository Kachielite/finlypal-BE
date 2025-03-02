package com.derrick.finlypal.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class TokenGenerator {
    private static final int TOKEN_LENGTH = 64;

    public static String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes =
                new byte
                        [TOKEN_LENGTH
                        / 2]; // Because each byte is represented by 2 characters in hexadecimal format
        random.nextBytes(tokenBytes);
        return new BigInteger(1, tokenBytes).toString(32);
    }

    public static String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }
}
