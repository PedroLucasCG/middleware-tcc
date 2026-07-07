package synchronization.domain;

import transport.domain.NodeConfig;

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
        this.transactionId = UUID.randomUUID();
        this.versionVector = new VersionVector();
    }

    public TransactionRecord(Annotation annotation, UUID nodeId, VersionVector versionVector) {
        this.annotation = annotation;
        this.nodeIdFromIncomingMessage = nodeId;
        this.versionVector = versionVector;
        this.transactionId = UUID.randomUUID();
    }

    // usado para criar uma anotação que está vindo da rede
    // e deve ser comparada com uma anotação local de mesmo id se existir
    // posto por causa da serialização que precisa de um construtor mas não pode fazer a validação no banco de dados
    public TransactionRecord(String value, Boolean deleted, UUID nodeIdFromIncomingMessage, UUID annotationId) {
        this.annotation = new Annotation(annotationId, value, deleted);
        this.nodeIdFromIncomingMessage = nodeIdFromIncomingMessage;
        this.transactionId = UUID.randomUUID();
    }

    public TransactionRecord(String value, Boolean deleted, UUID nodeIdFromIncomingMessage, UUID annotationId, VersionVector versionVector) {
        this.annotation = new Annotation(annotationId, value, deleted);
        this.nodeIdFromIncomingMessage = nodeIdFromIncomingMessage;
        this.transactionId = UUID.randomUUID();
        this.versionVector = versionVector;
    }

    // caso a anotação não exista localmente este construtor a cria a partir da anotação vinda da rede
    public TransactionRecord(TransactionRecord incomingRecord) {
        this.annotation = new Annotation(
                incomingRecord.getAnnotationId(),
                incomingRecord.getMessage(),
                incomingRecord.isDeleted());
        this.nodeIdFromIncomingMessage = incomingRecord.getNodeId();
        this.transactionId = incomingRecord.getTransactionId();
        this.versionVector = new VersionVector(incomingRecord.versionVector.getVersions());
    }

    public VersionVector getVersionVector() {
        if (versionVector == null) {
            versionVector = new VersionVector();
        }
        return versionVector;
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

    public void upsertVersion() {
        this.versionVector = versionVector.incremented(NodeConfig.defaults().nodeId());
    }

    public VectorRelation versionVectorCompare(TransactionRecord other) {
        return this.versionVector.compare(other.getVersionVector());
    }
}
