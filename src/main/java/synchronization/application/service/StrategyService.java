package synchronization.application.service;

import synchronization.domain.TransactionRecord;

import java.util.Map;
import java.util.Optional;

public interface StrategyService {
    void upsertMessage(TransactionRecord transactionRecord);
    TransactionRecord readMessage(String peerId, byte[] payload);
    void start();
}
