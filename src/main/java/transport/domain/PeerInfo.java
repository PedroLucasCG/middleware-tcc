package transport.domain;

import java.time.Instant;
import java.util.UUID;

public record PeerInfo(UUID id, String address, Instant lastSeen) {}