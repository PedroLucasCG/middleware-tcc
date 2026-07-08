package synchronization.application.listener;

import synchronization.domain.StrategyType;
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
        this.lastUpdate = Instant.parse(parts[1]);
        this.message = parts[2];
        this.nodeIdFromIncomingMessage = UUID.fromString(parts[3]);
        this.deleted = Boolean.parseBoolean(parts[4]);
        this.transactionId = UUID.fromString(parts[5]);
        this.annotationId = UUID.fromString(parts[6]);
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
