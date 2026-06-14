package synchronization.infra;

import synchronization.domain.TransactionRecord;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapStore implements RecordStore {
    private final Map<String, TransactionRecord> records = new ConcurrentHashMap<>();

    @Override
    public void mergeIncomingRecord(TransactionRecord transactionRecord) {
        records.merge(
                transactionRecord.getId(),
                transactionRecord,
                this::resolve
        );
    }

    @Override
    public TransactionRecord getTransactionRecordById(UUID transactionId) {
        return records.get(transactionId.toString());
    }

    @Override
    public Map<String, TransactionRecord> getAllTransactionRecords() {
        return records;
    }

    @Override
    public void addTransactionRecord(TransactionRecord transactionRecord) {
        records.put(transactionRecord.getId(), transactionRecord);
    }

    @Override
    public void deleteTransactionRecordById(UUID transactionId) {
        records.remove(transactionId.toString());
    }

    @Override
    public void updateTransactionRecord(TransactionRecord transactionRecord) {
        records.put(transactionRecord.getId(), transactionRecord);
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
