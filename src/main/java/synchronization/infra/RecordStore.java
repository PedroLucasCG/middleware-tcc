package synchronization.infra;

import synchronization.domain.TransactionRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RecordStore {
    void mergeIncomingRecord(TransactionRecord transactionRecord);
    TransactionRecord getTransactionRecordById(UUID transactionId);
    Map<String, TransactionRecord> getAllTransactionRecords();
    void addTransactionRecord(TransactionRecord transactionRecord);
    void deleteTransactionRecordById(UUID transactionId);
    void updateTransactionRecord(TransactionRecord transactionRecord);
}
