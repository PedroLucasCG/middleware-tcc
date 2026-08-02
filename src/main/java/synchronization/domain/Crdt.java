package synchronization.domain;

import java.time.Instant;
import java.util.*;

public class Crdt {
    private UUID operationId;
    private CrdtOperationType type;
    private long counter;
    private UUID nodeId;
    private String content;
    private UUID targetOperationId;
    private Instant timestamp;

    public Crdt(
            CrdtOperationType type,
            long counter,
            UUID nodeId,
            String content,
            UUID targetOperationId
    ) {
        this.operationId = UUID.randomUUID();
        this.type = type;
        this.nodeId = nodeId;
        this.counter = counter;
        this.content = content;
        this.targetOperationId = targetOperationId;
        this.timestamp = Instant.now();
    }

    private Crdt(
            UUID operationId,
            CrdtOperationType type,
            UUID nodeId,
            long counter,
            Instant timestamp
    ) {
        this.operationId = operationId;
        this.nodeId = nodeId;
        this.type = type;
        this.counter = counter;
        this.timestamp = timestamp;
    }

    public CrdtInfo getCrdtInfo() {
        return new CrdtInfo(this.operationId, this.type, this.counter, this.timestamp, this.nodeId);
    }

    public String serialize() {
        return String.join(",",
                operationId.toString(),
                type.toString(),
                String.valueOf(counter),
                timestamp.toString(),
                nodeId.toString()
        );
    }

    public static Crdt deserialize(String value) {
        String[] parts = value.split(",");

        return new Crdt(
                UUID.fromString(parts[0]),
                CrdtOperationType.valueOf(parts[1]),
                UUID.fromString(parts[4]),
                Long.parseLong(parts[2]),
                Instant.parse(parts[3])
        );
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public Long getCounter() {
        return counter;
    }

    public CrdtOperationType getType() {
        return type;
    }

    public UUID  getOperationId() {
        return operationId;
    }
}
