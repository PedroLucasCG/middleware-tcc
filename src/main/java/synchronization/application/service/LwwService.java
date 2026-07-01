package synchronization.application.service;

import shared.utils.ByteMessageHandler;
import synchronization.application.listener.LwwDTO;
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
    public void upsertMessage(TransactionRecord transactionRecord) {
        System.out.println(transactionRecord.getMessage());
        var data = new LwwDTO(transactionRecord);
        controller.broadcast(
                ByteMessageHandler.serialize(data)
        );
        System.out.println(
                "SENDING id=" + transactionRecord.getAnnotationId() +
                " value=" + transactionRecord.getMessage() +
                " time=" + transactionRecord.getUpdatedAt()
        );
        recordStore.addTransactionRecord(transactionRecord);
    }

    @Override
    public TransactionRecord readMessage(String peerId, byte[] payload) {
        String value = new String(payload, StandardCharsets.UTF_8);
        TransactionRecord incomingRecord = ByteMessageHandler.deserialize(value);

        snapshot().forEach((id, record) -> {
            System.out.println(
                    "SNAPSHOT id=" + id +
                    " value=" + record.getMessage() +
                    " time=" + record.getUpdatedAt()
            );
        });

        return recordStore.mergeIncomingRecord(incomingRecord, new LwwConflictResolver());
    }

    @Override
    public void start(StrategyMiddleware listener) {
        controller.start(listener);
    }

    private Map<UUID, TransactionRecord> snapshot() {
        return Map.copyOf(recordStore.getAllTransactionRecords());
    }
}
