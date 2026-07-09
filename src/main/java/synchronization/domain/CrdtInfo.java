package synchronization.domain;

import java.time.Instant;
import java.util.UUID;

public record CrdtInfo(
    UUID operationId,
    CrdtOperationType type,
    long counter,
    Instant timestamp) {
}
