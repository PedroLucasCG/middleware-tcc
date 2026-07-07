package synchronization.application.service;

import synchronization.application.listener.StrategyDTO;
import synchronization.application.listener.StrategyMiddleware;
import synchronization.domain.TransactionRecord;

public class CrdtService implements SynchronizationService {

    @Override
    public void upsertMessage(TransactionRecord transactionRecord) {

    }

    @Override
    public TransactionRecord readMessage(String peerId, byte[] payload) {
        return null;
    }

    @Override
    public void start(StrategyMiddleware listener) {

    }
}
