package synchronization.application.service;

import synchronization.domain.Middleware;
import synchronization.domain.TransactionRecord;
import transport.infra.TransportLayer;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LwwService implements StrategyService {
    private final TransportLayer transportLayer;

    public LwwService(TransportLayer transportLayer) {
        this.transportLayer = transportLayer;
    }

    @Override
    public void upsertMessage(TransactionRecord transactionRecord) {
        transportLayer.broadcast(
                Middleware.serialize(transactionRecord)
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
        TransactionRecord transactionRecord = Middleware.deserialize(value);

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


    private final Map<String, TransactionRecord> records = new ConcurrentHashMap<>();

    public void apply(TransactionRecord incoming) {
        records.merge(
                incoming.getId(),
                incoming,
                this::resolve
        );
    }

    private Optional<TransactionRecord> findById(String id) {
        return Optional.ofNullable(records.get(id));
    }

    private Map<String, TransactionRecord> snapshot() {
        return Map.copyOf(records);
    }

    private TransactionRecord resolve(TransactionRecord local, TransactionRecord incoming) {
        int timeComparison = incoming.getUpdatedAt().compareTo(local.getUpdatedAt());

        if (timeComparison > 0) {
            return incoming;
        }

        if (timeComparison < 0) {
            return local;
        }

        if (incoming.getNodeId().compareTo(local.getNodeId()) > 0) {
            return incoming;
        }

        return local;
    }
}
