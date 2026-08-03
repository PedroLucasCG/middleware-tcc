package synchronization.domain;

import transport.domain.NodeConfig;

import java.time.Instant;
import java.util.*;

public class TransactionRecord {
    private UUID transactionId;
    private TransactionContent transactionContent;
    private UUID nodeIdFromIncomingMessage;
    private VersionVector versionVector;
    private CrdtState crdtState;

    public TransactionRecord(TransactionContent transactionContent, UUID nodeId) {
        this.transactionContent = transactionContent;
        this.nodeIdFromIncomingMessage = nodeId;
        this.transactionId = UUID.randomUUID();
        this.versionVector = new VersionVector();
    }

    public TransactionRecord(TransactionContent transactionContent, UUID nodeId, VersionVector versionVector) {
        this.transactionContent = transactionContent;
        this.nodeIdFromIncomingMessage = nodeId;
        this.versionVector = versionVector;
        this.transactionId = UUID.randomUUID();
    }

    public TransactionRecord(TransactionContent transactionContent, UUID nodeId, Map<UUID, Set<Crdt>> operations) {
        this.transactionContent = transactionContent;
        this.nodeIdFromIncomingMessage = nodeId;
        this.crdtState = new CrdtState(operations);
        this.transactionId = UUID.randomUUID();
    }

    // usado para criar uma anotação que está vindo da rede
    // e deve ser comparada com uma anotação local de mesmo id se existir
    // posto por causa da serialização que precisa de um construtor mas não pode fazer a validação no banco de dados
    public TransactionRecord(String value, Boolean deleted, UUID nodeIdFromIncomingMessage, UUID annotationId) {
        this.transactionContent = new TransactionContent(annotationId, value, deleted);
        this.nodeIdFromIncomingMessage = nodeIdFromIncomingMessage;
        this.transactionId = UUID.randomUUID();
    }

    public TransactionRecord(String value, Boolean deleted, UUID nodeIdFromIncomingMessage, UUID annotationId, VersionVector versionVector) {
        this.transactionContent = new TransactionContent(annotationId, value, deleted);
        this.nodeIdFromIncomingMessage = nodeIdFromIncomingMessage;
        this.transactionId = UUID.randomUUID();
        this.versionVector = versionVector;
    }

    public TransactionRecord(
            String value,
            Boolean deleted,
            UUID nodeIdFromIncomingMessage,
            UUID annotationId,
            Map<UUID,
            Set<Crdt>> operations,
            Long operationStringIndex) {
        this.transactionContent = new TransactionContent(annotationId, value, deleted, operationStringIndex);
        this.nodeIdFromIncomingMessage = nodeIdFromIncomingMessage;
        this.transactionId = UUID.randomUUID();
        this.crdtState = new  CrdtState(operations);
    }

    public TransactionRecord(
            String value,
            Boolean deleted,
            UUID nodeIdFromIncomingMessage,
            UUID annotationId,
            Map<UUID,
            Set<Crdt>> operations) {
        this.transactionContent = new TransactionContent(annotationId, value, deleted);
        this.nodeIdFromIncomingMessage = nodeIdFromIncomingMessage;
        this.transactionId = UUID.randomUUID();
        this.crdtState = new  CrdtState(operations);
    }

    // caso a anotação não exista localmente este construtor a cria a partir da anotação vinda da rede
    public TransactionRecord(TransactionRecord incomingRecord) {
        this.transactionContent = new TransactionContent(
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
        return transactionContent.getId();
    }

    public UUID getTransactionId() {
        return this.transactionId;
    }

    public Instant getUpdatedAt() {
        return transactionContent.getUpdated();
    }

    public UUID getNodeId() {
        return nodeIdFromIncomingMessage;
    }

    public String getMessage() {
        return transactionContent.getValue();
    }

    public boolean isDeleted() {
        return transactionContent.isDeleted();
    }

    public void upsertVersion() {
        this.versionVector = versionVector.incremented(NodeConfig.defaults().nodeId());
    }

    public VectorRelation versionVectorCompare(TransactionRecord other) {
        return this.versionVector.compare(other.getVersionVector());
    }

    public void mergeVersionVectorReplicas(TransactionRecord other) {
        this.versionVector.merged(other.getVersionVector());
    }

    public List<CrdtInfo> getCrdtInfo() {
        return crdtState.getCrdtInfo();
    }

    public void crdtAddOperationForAnnotation(CrdtOperationType operation) {
        UUID nodeId = NodeConfig.defaults().nodeId();
        Set<Crdt> operations = crdGetOperationsByAnnotationId();

        long nextCounter = operations.stream()
                .filter(crdt -> crdt.getNodeId().equals(nodeId))
                .mapToLong(Crdt::getCounter)
                .max()
                .orElse(0L) + 1;

        UUID targetOperationId = getOperationIdByIndex(transactionContent.getOperationStringIndex());

        Crdt crdt = new Crdt(operation, nextCounter, nodeId, transactionContent.getValue(), targetOperationId);
        crdtState.addOperation(crdt, transactionContent.getId());
    }

    public void mergeCrdtOperations(TransactionRecord incoming) {
        crdtState.mergeOperations(incoming.crdtGetAll());
    }

    public Set<Crdt> crdGetOperationsByAnnotationId() {
        return this.crdtState.getOperations(this.transactionContent.getId());
    }

    public Map<UUID, Set<Crdt>> crdtGetAll(){
        return this.crdtState.getAll();
    }

    public Long getOperationStringIndex() {
        return transactionContent.getOperationStringIndex();
    }

    private UUID getOperationIdByIndex(long insertionIndex) {
        if (insertionIndex <= 0) {
            return null;
        }

        List<Crdt> visibleOperations = crdGetOperationsByAnnotationId()
                .stream()
                .filter(operation ->
                        operation.getType() == CrdtOperationType.INSERT)
                .sorted(
                        Comparator.comparingLong(Crdt::getCounter)
                                .thenComparing(Crdt::getNodeId)
                                .thenComparing(Crdt::getOperationId)
                )
                .toList();

        int predecessorIndex = (int) insertionIndex - 1;

        if (predecessorIndex >= visibleOperations.size()) {
            throw new IndexOutOfBoundsException(
                    "Insertion index " + insertionIndex +
                            " is invalid for a document with " +
                            visibleOperations.size() + " operations"
            );
        }

        return visibleOperations
                .get(predecessorIndex)
                .getOperationId();
    }
}
