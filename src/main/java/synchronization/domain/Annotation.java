package synchronization.domain;

import java.time.Instant;
import java.util.UUID;

public class Annotation {
    private UUID id;
    private String value;
    private boolean deleted;
    private Instant created;
    private Instant updated;

    public Annotation() {
    }

    public Annotation(String value) {
        this.id = UUID.randomUUID();
        this.value = value;
        this.deleted = false;
        this.created = Instant.now();
        this.updated = Instant.now();
    }

    public Annotation(String value, UUID id) {
        this.id = id;
        this.value = value;
        this.deleted = false;
        this.created = Instant.now();
        this.updated = Instant.now();
    }

    public Annotation(UUID id, String value, Boolean deleted) {
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
