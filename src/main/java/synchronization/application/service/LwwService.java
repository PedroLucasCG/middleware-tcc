package synchronization.application.service;

import shared.utils.ByteMessageHandler;
import synchronization.domain.TransactionRecord;
import synchronization.infra.ConcurrentHashMapStore;
import synchronization.infra.RecordStore;
import transport.infra.TransportLayer;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LwwService implements StrategyService {
    private final TransportLayer transportLayer;
    private final RecordStore recordStore;

    public LwwService(TransportLayer transportLayer, RecordStore recordStore) {
        this.transportLayer = transportLayer;
        this.recordStore = recordStore;
    }

    @Override
    public void upsertMessage(TransactionRecord transactionRecord) {
        transportLayer.broadcast(
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
    public void start() {
        transportLayer.start();
    }

    public void apply(TransactionRecord transactionRecord) {
        recordStore.mergeIncomingRecord(transactionRecord);
    }

    private Map<String, TransactionRecord> snapshot() {
        return Map.copyOf(recordStore.getAllTransactionRecords());
    }
}
