package synchronization.application.service;

import shared.utils.ByteMessageHandler;
import synchronization.application.infra.BroadcastController;
import synchronization.application.infra.RecordStore;
import synchronization.application.api.StrategyDTO;
import synchronization.application.api.StrategyMiddleware;
import synchronization.application.api.VersionVectorDTO;
import synchronization.domain.TransactionRecord;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class VersionVectorService implements SynchronizationService {
    private final BroadcastController controller;
    private final RecordStore recordStore;

    public VersionVectorService(BroadcastController controller, RecordStore recordStore) {
        this.controller = controller;
        this.recordStore = recordStore;
    }

    @Override
    public StrategyDTO upsertMessage(TransactionRecord transactionRecord) {
        transactionRecord.upsertVersion();
        var data = new VersionVectorDTO(transactionRecord);
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
        recordStore.mergeIncomingRecord(incomingRecord, new VersionVectorConflictResolver());
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
