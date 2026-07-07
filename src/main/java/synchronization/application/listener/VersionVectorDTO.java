package synchronization.application.listener;

import shared.utils.Base64Codec;
import shared.utils.VersionVectorStringParser;
import synchronization.domain.TransactionRecord;
import synchronization.domain.VersionVector;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VersionVectorDTO implements StrategyDTO {
    private final StrategyType typeDTO;
    private final Instant lastUpdate;
    private final String message;
    private final UUID nodeIdFromIncomingMessage;
    private final Boolean deleted;
    private final UUID transactionId;
    private final UUID annotationId;
    private final Map<UUID, Long> versions;

    public VersionVectorDTO(TransactionRecord transactionRecord) {
        this.typeDTO = StrategyType.VERSION_VECTOR;
        this.lastUpdate = transactionRecord.getUpdatedAt();
        this.message = transactionRecord.getMessage();
        this.nodeIdFromIncomingMessage = transactionRecord.getNodeId();
        this.deleted = transactionRecord.isDeleted();
        this.transactionId = transactionRecord.getTransactionId();
        this.annotationId = transactionRecord.getAnnotationId();
        this.versions = new HashMap<>(
                transactionRecord
                        .getVersionVector()
                        .getVersions()
        );
    }

    public VersionVectorDTO(String[] parts) {
        if (parts == null || parts.length != 8) {
            throw new IllegalArgumentException(
                    "VersionVectorDTO requires exactly 8 fields"
            );
        }

        this.typeDTO = StrategyType.VERSION_VECTOR;
        this.lastUpdate = Instant.parse(parts[1]);
        this.message = Base64Codec.decodeMessage(parts[2]);
        this.nodeIdFromIncomingMessage = UUID.fromString(parts[3]);
        this.deleted = Boolean.parseBoolean(parts[4]);
        this.transactionId = UUID.fromString(parts[5]);
        this.annotationId = UUID.fromString(parts[6]);
        this.versions = VersionVectorStringParser.stringToVersionVector(parts[7]);
    }

    @Override
    public String toString() {
        return String.join("|",
                this.typeDTO.toString(),
                this.lastUpdate.toString(),
                Base64Codec.encodeMessage(this.message),
                this.nodeIdFromIncomingMessage.toString(),
                String.valueOf(deleted),
                this.transactionId.toString(),
                this.annotationId.toString(),
                VersionVectorStringParser.mapToString(this.versions)
        );
    }

    @Override
    public TransactionRecord makeTransactionRecordFromDto() {
        return new TransactionRecord(
                this.message,
                this.deleted,
                this.nodeIdFromIncomingMessage,
                this.annotationId,
                new VersionVector(this.versions)
        );
    }
}
