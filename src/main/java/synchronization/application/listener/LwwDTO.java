package synchronization.application.listener;

import synchronization.domain.TransactionRecord;

import java.time.Instant;
import java.util.UUID;

public class LwwDTO implements StrategyDTO {
    private StrategyType typeDTO;
    private Instant lastUpdate;
    private String message;
    private UUID nodeIdFromIncomingMessage;
    private Boolean deleted;
    private UUID transactionId;
    private UUID annotationId;

    public LwwDTO(TransactionRecord transactionRecord) {
        this.typeDTO = StrategyType.LWW;
        this.lastUpdate = transactionRecord.getUpdatedAt();
        this.message = transactionRecord.getMessage();
        this.nodeIdFromIncomingMessage = transactionRecord.getNodeId();
        this.deleted = transactionRecord.isDeleted();
        this.transactionId = transactionRecord.getTransactionId();
        this.annotationId = transactionRecord.getAnnotationId();
    }

    public LwwDTO(String[] parts) {
        this.typeDTO = StrategyType.LWW;
        this.lastUpdate = Instant.parse(parts[0]);
        this.message = parts[1];
        this.nodeIdFromIncomingMessage = UUID.fromString(parts[2]);
        this.deleted = Boolean.parseBoolean(parts[3]);
        this.transactionId = UUID.fromString(parts[4]);
        this.annotationId = UUID.fromString(parts[5]);
    }

    @Override
    public String toString() {
        return String.join("|",
                this.typeDTO.toString(),
                this.lastUpdate.toString(),
                this.message,
                this.nodeIdFromIncomingMessage.toString(),
                String.valueOf(deleted),
                this.transactionId.toString(),
                this.annotationId.toString()
        );
    }

    @Override
    public TransactionRecord makeTransactionRecordFromDto() {
        return new TransactionRecord(this.message, this.deleted, this.nodeIdFromIncomingMessage, this.annotationId);
    }

}
