package transport.infra;

import shared.utils.Base64Coder;
import transport.domain.WireMessage;

import java.time.Instant;
import java.util.Optional;

public class WireMessageCodec {

    private static final String HELLO = "HELLO";
    private static final String MSG = "MSG";

    public String encodeHello(String senderId) {
        return HELLO + " " + senderId + " " + Instant.now();
    }

    public String encodeMsg(String senderId, byte[] payload) {
        return MSG + " " + senderId + " " + Base64Coder.encode(payload);
    }

    public Optional<WireMessage> decode(String raw) {
        String[] parts = raw.split(" ", 4);

        if (parts.length < 2) {
            return Optional.empty();
        }

        String type = parts[0];
        String senderId = parts[1];

        return switch (type) {
            case HELLO -> decodeHello(senderId, parts);
            case MSG -> decodeMsg(senderId, parts);
            default -> Optional.empty();
        };
    }

    private Optional<WireMessage> decodeHello(String senderId, String[] parts) {
        if (parts.length < 3) {
            return Optional.empty();
        }

        try {
            Instant timestamp = Instant.parse(parts[2]);
            return Optional.of(new WireMessage.Hello(senderId, timestamp));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<WireMessage> decodeMsg(String senderId, String[] parts) {
        if (parts.length < 3) {
            return Optional.empty();
        }

        byte[] payload = Base64Coder.decode(parts[2]);
        return Optional.of(new WireMessage.Msg(senderId, payload));
    }
}