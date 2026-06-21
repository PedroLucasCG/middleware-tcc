package synchronization.application.service;

import shared.utils.ByteMessageHandler;
import synchronization.application.listener.StrategyMiddleware;
import synchronization.domain.TransactionRecord;
import synchronization.infra.RecordStore;
import transport.aplication.controller.BroadcastController;
import transport.infra.TransportLayer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LwwService implements SynchronizationService {
    private final BroadcastController controller;
    private final RecordStore recordStore;

    public LwwService(BroadcastController controller, RecordStore recordStore) {
        this.controller = controller;
        this.recordStore = recordStore;
    }

    @Override
    public void upsertMessage(TransactionRecord transactionRecord) {
        controller.broadcast(
                ByteMessageHandler.serialize(transactionRecord)
        );
        System.out.println(
                "SENDING id=" + transactionRecord.getId() +
                " value=" + transactionRecord.getValue() +
                " time=" + transactionRecord.getUpdatedAt()
        );

    }

    @Override
    public TransactionRecord readMessage(String peerId, byte[] payload) {
        String value = new String(payload, StandardCharsets.UTF_8);
        TransactionRecord transactionRecord = ByteMessageHandler.deserialize(value);

        snapshot().forEach((id, record) -> {
            System.out.println(
                    "SNAPSHOT id=" + id +
                    " value=" + record.getValue() +
                    " time=" + record.getUpdatedAt()
            );
        });
        apply(transactionRecord);
        return transactionRecord;
    }

    @Override
    public void start(StrategyMiddleware listener) {
        controller.start(listener);
    }

    private void apply(TransactionRecord transactionRecord) {
        recordStore.mergeIncomingRecord(transactionRecord);
    }

    private Map<String, TransactionRecord> snapshot() {
        return Map.copyOf(recordStore.getAllTransactionRecords());
    }
}
