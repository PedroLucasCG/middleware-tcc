package synchronization.domain;

import java.time.Instant;

public class TransactionRecord {
    private Annotation annotation;
    private String nodeId;

    public TransactionRecord(Annotation annotation, String nodeId) {
        this.annotation = annotation;
        this.nodeId = nodeId;
    }

    public String getId() {
        return annotation.getId().toString();
    }

    public Instant getUpdatedAt() {
        return annotation.getUpdated();
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getMessage() {
        return annotation.getValue();
    }

    public boolean isDeleted() {
        return annotation.isDeleted();
    }
}
