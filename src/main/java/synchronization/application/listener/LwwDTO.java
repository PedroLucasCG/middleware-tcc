package synchronization.application.listener;

import java.time.Instant;
import java.util.UUID;

public class LwwDTO {
    private Instant lastUpdate;
    private String message;
    private UUID nodeIdFromIncomingMessage;
    private Boolean deleted;
    private UUID transactionId;

    public LwwDTO(Instant lastUpdate, String message, UUID nodeIdFromIncomingMessage, Boolean deleted, UUID transactionId) {
        this.lastUpdate = lastUpdate;
        this.message = message;
        this.nodeIdFromIncomingMessage = nodeIdFromIncomingMessage;
        this.deleted = deleted;
        this.transactionId = transactionId;
    }
}
