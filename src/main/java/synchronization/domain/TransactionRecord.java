package synchronization.domain;

import java.time.Instant;
import java.util.UUID;

public class TransactionRecord {
    private UUID transactionId;
    private Annotation annotation;
    private UUID nodeIdFromIncomingMessage;
    private VersionVector versionVector;

    public TransactionRecord(Annotation annotation, UUID nodeId) {
        this.annotation = annotation;
        this.nodeIdFromIncomingMessage = nodeId;
    }

    public TransactionRecord(Annotation annotation, UUID nodeId, VersionVector versionVector) {
        this.annotation = annotation;
        this.nodeIdFromIncomingMessage = nodeId;
        this.versionVector = versionVector;
    }

    // usado para criar uma anotação que está vindo da rede
    // e deve ser comparada com uma anotação local de mesmo id se existir
    // posto por causa da serialização que precisa de um construtor mas não pode fazer a validação no banco de dados
    public TransactionRecord(String value, Boolean deleted, UUID nodeIdFromIncomingMessage, UUID annotationId) {
        this.annotation = new Annotation(annotationId, value, deleted);
        this.nodeIdFromIncomingMessage = nodeIdFromIncomingMessage;
        this.transactionId = UUID.randomUUID();
    }

    public UUID getAnnotationId() {
        return annotation.getId();
    }

    public UUID getTransactionId() {
        return this.transactionId;
    }

    public Instant getUpdatedAt() {
        return annotation.getUpdated();
    }

    public UUID getNodeId() {
        return nodeIdFromIncomingMessage;
    }

    public String getMessage() {
        return annotation.getValue();
    }

    public boolean isDeleted() {
        return annotation.isDeleted();
    }
}
