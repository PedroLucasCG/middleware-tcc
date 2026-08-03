package synchronization.application.listener;

import shared.utils.StrategyDTOStringParser;
import synchronization.domain.Crdt;
import synchronization.domain.StrategyType;
import synchronization.domain.TransactionRecord;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CrdtDTO implements StrategyDTO{
    private StrategyType typeDTO;
    private Instant lastUpdate;
    private String message;
    private UUID nodeIdFromIncomingMessage;
    private Boolean deleted;
    private UUID transactionId;
    private UUID transactionContentId;
    private Long operationStringIndex;
    private Map<UUID, Set<Crdt>> operations;

    public CrdtDTO(TransactionRecord transactionRecord) {
        this.typeDTO = StrategyType.CRDT;
        this.lastUpdate = transactionRecord.getUpdatedAt();
        this.message = transactionRecord.getMessage();
        this.nodeIdFromIncomingMessage = transactionRecord.getNodeId();
        this.deleted = transactionRecord.isDeleted();
        this.transactionId = transactionRecord.getTransactionId();
        this.operationStringIndex = transactionRecord.getOperationStringIndex();
        this.transactionContentId = transactionRecord.getAnnotationId();
    }

    public CrdtDTO(String[] parts) {
        this.typeDTO = StrategyType.CRDT;
        this.lastUpdate = Instant.parse(parts[1]);
        this.message = parts[2];
        this.nodeIdFromIncomingMessage = UUID.fromString(parts[3]);
        this.deleted = Boolean.parseBoolean(parts[4]);
        this.transactionId = UUID.fromString(parts[5]);
        this.transactionContentId = UUID.fromString(parts[6]);
        this.operationStringIndex = Long.valueOf(parts[7]);
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
                this.transactionContentId.toString(),
                StrategyDTOStringParser.getCrdtSerializedOperations(operations),
                this.operationStringIndex.toString()
        );
    }

    @Override
    public TransactionRecord makeTransactionRecordFromDto() {
        return new TransactionRecord(
                this.message,
                this.deleted,
                this.nodeIdFromIncomingMessage,
                this.transactionContentId,
                this.operations,
                this.operationStringIndex
        );
    }
}
