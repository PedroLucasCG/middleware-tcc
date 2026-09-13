package synchronization.application.service;

import synchronization.application.api.StrategyDTO;
import synchronization.application.api.StrategyMiddleware;
import synchronization.domain.TransactionRecord;

public interface SynchronizationService {
    StrategyDTO upsertMessage(TransactionRecord transactionRecord);
    StrategyDTO readMessage(String peerId, byte[] payload);
    void start(StrategyMiddleware listener);
}
