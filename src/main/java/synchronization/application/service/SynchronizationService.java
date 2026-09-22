package synchronization.application.service;

import synchronization.application.api.StrategyDTO;
import synchronization.application.api.StrategyMiddleware;
import synchronization.application.infra.BroadcastController;
import synchronization.application.infra.RecordStore;
import synchronization.domain.StrategyType;
import synchronization.domain.TransactionRecord;

public interface SynchronizationService {
    StrategyDTO upsertMessage(TransactionRecord transactionRecord);
    StrategyDTO readMessage(String peerId, byte[] payload);
    void start(StrategyMiddleware listener);

    static SynchronizationService getStrategyByType(
            StrategyType strategyType,
            BroadcastController controller,
            RecordStore recordStore) {
        return switch(strategyType) {
            case CRDT -> new CrdtService(controller, recordStore);
            case LWW -> new LwwService(controller, recordStore);
            case VERSION_VECTOR -> new VersionVectorService(controller, recordStore);
            default -> throw new IllegalStateException("Unexpected value: " + strategyType);
        };
    }
}
