package synchronization.application.infra;

import synchronization.domain.TransactionRecord;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface RecordStore {
    void mergeIncomingRecord(TransactionRecord transactionRecord);
    TransactionRecord getTransactionRecordById(UUID transactionId);
    Optional<TransactionRecord> getTransactionRecordByAnnotationId(UUID annotationId);
    Map<UUID, TransactionRecord> getAllTransactionRecords();
    void addTransactionRecord(TransactionRecord transactionRecord);
    void deleteTransactionRecordById(UUID transactionId);
    void updateTransactionRecord(TransactionRecord transactionRecord);
}
