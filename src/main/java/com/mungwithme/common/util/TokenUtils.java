package com.mungwithme.common.util;

/**
 *
 *  토큰 생성
 *
 */
import java.security.SecureRandom;

public class TokenUtils {

    private static final int SALT_SIZE = 28;

    // 바이트 값을 16진수로 변경해준다
    private static String Byte_to_String(byte[] temp) {
        StringBuilder sb = new StringBuilder();
        for (byte a : temp) {
            sb.append(String.format("%02x", a));
        }
        return sb.toString();
    }

    // SALT 값 생성
    public static String getToken() {
        try {
            SecureRandom rnd = new SecureRandom();
            byte[] temp = new byte[SALT_SIZE];
            rnd.nextBytes(temp);
            return Byte_to_String(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // SALT 값 생성
    public static String getRedisAuthToken() {
        try {
            SecureRandom rnd = new SecureRandom();
            byte[] temp = new byte[10];
            rnd.nextBytes(temp);
            return Byte_to_String(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
