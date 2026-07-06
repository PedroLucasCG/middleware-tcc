package shared.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Codec {
    public static String encode(byte[] payload) {
        return Base64.getEncoder().encodeToString(payload);
    }

    public static byte[] decode(String payload) {
        return Base64.getDecoder().decode(payload);
    }

    public static String encodeMessage(String message) {
        if (message == null) {
            return "";
        }

        return Base64.getEncoder().encodeToString(
                message.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static String decodeMessage(String encodedMessage) {
        if (encodedMessage == null || encodedMessage.isEmpty()) {
            return "";
        }

        return new String(
                Base64.getDecoder().decode(encodedMessage),
                StandardCharsets.UTF_8
        );
    }
}
