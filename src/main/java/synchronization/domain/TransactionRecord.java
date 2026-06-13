package synchronization.domain;

import java.time.Instant;

public class TransactionRecord {
    private String id;
    private String value;
    private Instant updatedAt;
    private String nodeId;
    private boolean deleted;

    public TransactionRecord(String id, String value, Instant updatedAt, String nodeId, boolean deleted) {
        this.id = id;
        this.value = value;
        this.updatedAt = updatedAt;
        this.nodeId = nodeId;
        this.deleted = deleted;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getNodeId() {
        return nodeId;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
