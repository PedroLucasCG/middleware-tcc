package synchronization.application.listener;

import synchronization.application.service.SynchronizationService;
import synchronization.domain.TransactionRecord;
import transport.domain.NodeConfig;
import transport.domain.PeerInfo;

import java.time.Instant;

public class Listener implements StrategyMiddleware {
    private final SynchronizationService synchronizationService;

    public Listener(SynchronizationService synchronizationService) {
        this.synchronizationService = synchronizationService;
    }

    @Override
    public void start() {
        synchronizationService.start(this);
    }

    @Override
    public void createOrUpdate(TransactionRecord transactionRecord) {
        synchronizationService.upsertMessage(transactionRecord);
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
        TransactionRecord transactionRecord = synchronizationService.readMessage(peerId, payload);

        logEvent("MESSAGE_RECEIVED", peerId, transactionRecord.getValue());
    }

    public void logEvent(String event, String target, String message) {
        System.out.printf(
                "{\"time\":\"%s\",\"peer\":\"%s\",\"event\":\"%s\",\"target\":\"%s\",\"message\":\"%s\"}%n",
                Instant.now(),
                NodeConfig.defaults().nodeId(),
                event,
                target,
                message
        );
    }
}
