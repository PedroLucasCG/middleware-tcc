package synchronization.domain;

import java.time.Instant;
import java.util.UUID;

public class TransactionRecord {
    private UUID transactionId;
    private Annotation annotation;
    private String nodeId;
    private VersionVector versionVector;

    public TransactionRecord(Annotation annotation, String nodeId) {
        this.annotation = annotation;
        this.nodeId = nodeId;
    }

    public TransactionRecord(Annotation annotation, String nodeId, VersionVector versionVector) {
        this.annotation = annotation;
        this.nodeId = nodeId;
        this.versionVector = versionVector;
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
