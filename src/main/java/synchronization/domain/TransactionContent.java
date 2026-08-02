package synchronization.domain;

import java.time.Instant;
import java.util.UUID;

public class TransactionContent {
    private UUID id;
    private String value;
    private boolean deleted;
    private Long operationStringIndex;
    private Instant created;
    private Instant updated;

    public TransactionContent() {
    }

    public TransactionContent(String value) {
        this.id = UUID.randomUUID();
        this.value = value;
        this.deleted = false;
        this.created = Instant.now();
        this.updated = Instant.now();
    }

    public TransactionContent(String value, UUID id) {
        this.id = id;
        this.value = value;
        this.deleted = false;
        this.created = Instant.now();
        this.updated = Instant.now();
    }

    public TransactionContent(String value, UUID id, Long operationStringIndex) {
        this.id = id;
        this.value = value;
        this.deleted = false;
        this.operationStringIndex = operationStringIndex;
        this.created = Instant.now();
        this.updated = Instant.now();
    }

    public TransactionContent(UUID id, String value, Boolean deleted) {
        this.value = value;
        this.deleted = deleted;
        this.id = id;
        this.created = Instant.now();
        this.updated = Instant.now();
    }

    public String getValue() {
        return value;
    }

    public UUID getId() {
        return id;
    }

    public Long  getOperationStringIndex() {
        return operationStringIndex;
    }

    public boolean isDeleted() {
        this.updated = Instant.now();
        return deleted;
    }

    public Instant getCreated() {
        return this.created;
    }

    public Instant getUpdated() {
        return this.updated == null ? this.created : this.updated;
    }

    public void deleteMessage() {
        this.deleted = true;
        this.updated = Instant.now();
    }
}
