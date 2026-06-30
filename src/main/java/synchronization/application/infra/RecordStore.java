package synchronization.application.infra;

import synchronization.domain.ConflictResolver;
import synchronization.domain.TransactionRecord;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface RecordStore {
    TransactionRecord mergeIncomingRecord(TransactionRecord transactionRecord, ConflictResolver conflictResolver);
    TransactionRecord getTransactionRecordById(UUID transactionId);
    Optional<TransactionRecord> getTransactionRecordByAnnotationId(UUID annotationId);
    Map<UUID, TransactionRecord> getAllTransactionRecords();
    void addTransactionRecord(TransactionRecord transactionRecord);
    void deleteTransactionRecordById(UUID transactionId);
    void updateTransactionRecord(TransactionRecord transactionRecord);
}
