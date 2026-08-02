package synchronization.infra;

import synchronization.application.infra.RecordStore;
import synchronization.application.service.ConflictResolver;
import synchronization.domain.TransactionRecord;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionRecordHashMapStore implements RecordStore {
    private final Map<UUID, TransactionRecord> records = new ConcurrentHashMap<>();

    @Override
    public TransactionRecord mergeIncomingRecord(TransactionRecord transactionRecord, ConflictResolver conflictResolver) {
         return records.merge(
                transactionRecord.getAnnotationId(),
                transactionRecord,
                conflictResolver::resolve
         );
    }

    @Override
    public TransactionRecord getTransactionRecordById(UUID transactionId) {
        return records.get(transactionId.toString());
    }

    @Override
    public Optional<List<TransactionRecord>> getTransactionRecordsByTransactionContentId(UUID annotationId) {
        return Optional.of(records.values()
                .stream()
                .filter(record ->
                        record.getAnnotationId().equals(annotationId)
                )
                .collect(Collectors.<TransactionRecord>toList()));
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
}
