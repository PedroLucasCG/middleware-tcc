package synchronization.application.infra;

import synchronization.application.service.ConflictResolver;
import synchronization.domain.TransactionRecord;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface RecordStore {
    TransactionRecord mergeIncomingRecord(TransactionRecord transactionRecord, ConflictResolver conflictResolver);
    TransactionRecord getTransactionRecordById(UUID transactionId);
    Optional<List<TransactionRecord>> getTransactionRecordsByTransactionContentId(UUID annotationId);
    Map<UUID, TransactionRecord> getAllTransactionRecords();
    void addTransactionRecord(TransactionRecord transactionRecord);
    void deleteTransactionRecordById(UUID transactionId);
    void updateTransactionRecord(TransactionRecord transactionRecord);
}
