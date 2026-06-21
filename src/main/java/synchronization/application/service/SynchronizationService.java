package synchronization.application.service;

import synchronization.application.listener.StrategyMiddleware;
import synchronization.domain.TransactionRecord;

public interface SynchronizationService {
    void upsertMessage(TransactionRecord transactionRecord);
    TransactionRecord readMessage(String peerId, byte[] payload);
    void start(StrategyMiddleware listener);
}
