package synchronization.domain;

import java.time.Instant;
import java.util.*;

public class Crdt {
    private UUID operationId;
    private CrdtOperationType type;
    private long counter;
    private Instant timestamp;

    public Crdt(
            UUID operationId,
            CrdtOperationType type,
            long counter,
            Instant timestamp
    ) {
        this.operationId = operationId;
        this.type = type;
        this.counter = counter;
        this.timestamp = timestamp;
    }

    public CrdtInfo getCrdtInfo() {
        return new CrdtInfo(this.operationId, this.type, this.counter, this.timestamp);
    }

    public String serialize() {
        return String.join(",",
                operationId.toString(),
                type.toString(),
                String.valueOf(counter),
                timestamp.toString()
        );
    }

    public static Crdt deserialize(String value) {
        String[] parts = value.split(",");

        return new Crdt(
                UUID.fromString(parts[0]),
                CrdtOperationType.valueOf(parts[1]),
                Long.parseLong(parts[2]),
                Instant.parse(parts[3])
        );
    }
}
