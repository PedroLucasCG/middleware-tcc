package synchronization.infra;

import synchronization.application.infra.RecordStore;
import synchronization.domain.TransactionRecord;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapStore implements RecordStore {
    private final Map<UUID, TransactionRecord> records = new ConcurrentHashMap<>();

    @Override
    public void mergeIncomingRecord(TransactionRecord transactionRecord) {
        records.merge(
                transactionRecord.getAnnotationId(),
                transactionRecord,
                this::resolve
        );
    }

    @Override
    public TransactionRecord getTransactionRecordById(UUID transactionId) {
        return records.get(transactionId.toString());
    }

    @Override
    public Optional<TransactionRecord> getTransactionRecordByAnnotationId(UUID annotationId) {
        for (TransactionRecord transactionRecord : records.values()) {
            if (transactionRecord.getAnnotationId().equals(annotationId)) {
                return Optional.of(transactionRecord);
            }
        }
        return Optional.empty();
    }

    @Override
    public Map<UUID, TransactionRecord> getAllTransactionRecords() {
        return records;
    }

    @Override
    public void addTransactionRecord(TransactionRecord transactionRecord) {
        records.put(transactionRecord.getAnnotationId(), transactionRecord);
    }

    @Override
    public void deleteTransactionRecordById(UUID transactionId) {
        records.remove(transactionId.toString());
    }

    @Override
    public void updateTransactionRecord(TransactionRecord transactionRecord) {
        records.put(transactionRecord.getAnnotationId(), transactionRecord);
    }

    private TransactionRecord resolve(TransactionRecord local, TransactionRecord incoming) {
        int timeComparison = incoming.getUpdatedAt().compareTo(local.getUpdatedAt());

        if (timeComparison > 0) {
            return incoming;
        }

        if (timeComparison < 0) {
            return local;
        }

        if (incoming.getNodeId().compareTo(local.getNodeId()) > 0) {
            return incoming;
        }

        return local;
    }
}
