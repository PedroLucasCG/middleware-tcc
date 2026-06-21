package transport.domain;

import java.time.Instant;

public interface WireMessage {
    record Hello(String senderId, Instant timestamp) implements WireMessage {}

    record Msg(String senderId, byte[] payload) implements WireMessage {}
}
