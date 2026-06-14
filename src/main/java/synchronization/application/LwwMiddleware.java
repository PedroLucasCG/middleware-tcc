package synchronization.application;

import synchronization.domain.LwwStrategy;
import synchronization.domain.Middleware;
import synchronization.domain.TransactionRecord;
import transport.domain.PeerInfo;
import transport.infra.TransportLayer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;

public class LwwMiddleware extends Middleware implements StrategyMiddleware {
    private final LwwStrategy strategy = new LwwStrategy();

    public LwwMiddleware(TransportLayer transportLayer) {
        super(transportLayer);
        super.getTransportLayer().setListener(this);
    }

    @Override
    public void start() {
        super.getTransportLayer().start();
    }

    @Override
    public void createOrUpdate(TransactionRecord transactionRecord) {
        super.getTransportLayer().broadcast(
                super.serialize(transactionRecord)
        );
        System.out.println(
            "SENDING id=" + transactionRecord.getId() +
            " value=" + transactionRecord.getValue() +
            " time=" + transactionRecord.getUpdatedAt()
        );
        logEvent("MESSAGE_SENT", "ALL", transactionRecord.getValue());
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void onMessage(String peerId, String message) {

    }

    @Override
    public void onPeerDiscovered(PeerInfo peer) {
        logEvent("PEER_DISCOVERED", peer.id().toString(), peer.address());
    }

    @Override
    public void onPeerLost(String peerId) {

    }

    @Override
    public void onMessageReceived(String peerId, byte[] payload) {
        String value = new String(payload, StandardCharsets.UTF_8);
        TransactionRecord transactionRecord = super.deserialize(value);

        logEvent("MESSAGE_RECEIVED", peerId, transactionRecord.getValue());
        strategy.snapshot().forEach((id, record) -> {
            System.out.println(
                "SNAPSHOT id=" + id +
                " value=" + record.getValue() +
                " time=" + record.getUpdatedAt()
            );
        });
        strategy.apply(transactionRecord);
    }

    public void logEvent(String event, String target, String message) {
        System.out.printf(
                "{\"time\":\"%s\",\"peer\":\"%s\",\"event\":\"%s\",\"target\":\"%s\",\"message\":\"%s\"}%n",
                Instant.now(),
                Middleware.getNodeId(),
                event,
                target,
                message
        );
    }
}
