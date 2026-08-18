package synchronization.application.service;

import shared.utils.ByteMessageHandler;
import synchronization.application.listener.LwwDTO;
import synchronization.application.listener.StrategyDTO;
import synchronization.application.listener.StrategyMiddleware;
import synchronization.domain.TransactionRecord;
import synchronization.application.infra.RecordStore;
import synchronization.application.infra.BroadcastController;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class LwwService implements SynchronizationService {
    private final BroadcastController controller;
    private final RecordStore recordStore;

    public LwwService(BroadcastController controller, RecordStore recordStore) {
        this.controller = controller;
        this.recordStore = recordStore;
    }

    @Override
    public StrategyDTO upsertMessage(TransactionRecord transactionRecord) {
        var data = new LwwDTO(transactionRecord);
        controller.broadcast(
                ByteMessageHandler.serialize(data)
        );
        recordStore.addTransactionRecord(transactionRecord);

        return data;
    }

    @Override
    public StrategyDTO readMessage(String peerId, byte[] payload) {
        String value = new String(payload, StandardCharsets.UTF_8);
        StrategyDTO incomingDto = ByteMessageHandler.deserialize(value);
        TransactionRecord incomingRecord = incomingDto.makeTransactionRecordFromDto();
        recordStore.mergeIncomingRecord(incomingRecord, new LwwConflictResolver());
        return incomingDto;
    }

    @Override
    public void start(StrategyMiddleware listener) {
        controller.start(listener);
    }

    private Map<UUID, TransactionRecord> snapshot() {
        return Map.copyOf(recordStore.getAllTransactionRecords());
    }
}
