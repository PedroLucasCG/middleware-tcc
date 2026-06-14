package synchronization.application.listener;

import synchronization.application.service.LwwService;
import synchronization.application.service.StrategyService;
import synchronization.domain.Middleware;
import synchronization.domain.TransactionRecord;
import transport.domain.PeerInfo;
import transport.infra.TransportLayer;

import java.time.Instant;

public class Listener extends Middleware implements StrategyMiddleware {
    private StrategyService strategyService;

    public Listener(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @Override
    public void start() {
        strategyService.start();
    }

    @Override
    public void createOrUpdate(TransactionRecord transactionRecord) {
        strategyService.upsertMessage(transactionRecord);
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
        TransactionRecord transactionRecord = strategyService.readMessage(peerId, payload);

        logEvent("MESSAGE_RECEIVED", peerId, transactionRecord.getValue());
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
