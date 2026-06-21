package shared.utils;

import java.util.Base64;

public class Base64Coder {
    public static String encode(byte[] payload) {
        return Base64.getEncoder().encodeToString(payload);
    }

    public static byte[] decode(String payload) {
        return Base64.getDecoder().decode(payload);
    }
}
