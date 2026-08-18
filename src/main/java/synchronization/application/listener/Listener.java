package synchronization.application.listener;

import observer.application.api.ObserverAPI;
import observer.domain.EventState;
import synchronization.application.service.SynchronizationService;
import synchronization.domain.TransactionRecord;
import transport.domain.NodeConfig;
import transport.domain.PeerInfo;

import java.time.Instant;

public class Listener implements StrategyMiddleware {
    private final SynchronizationService synchronizationService;
    private final ObserverAPI observerAPI;

    public Listener(SynchronizationService synchronizationService, ObserverAPI observerAPI) {
        this.synchronizationService = synchronizationService;
        this.observerAPI = observerAPI;
    }

    @Override
    public void start() {
        synchronizationService.start(this);
        observerAPI.connect();
    }

    @Override
    public void createOrUpdate(TransactionRecord transactionRecord) {
        StrategyDTO strategyDTO = synchronizationService.upsertMessage(transactionRecord);
        logEvent(EventState.SENT, strategyDTO);
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void onMessage(String peerId, String message) {

    }

    @Override
    public void onPeerDiscovered(PeerInfo peer) {
        logEvent(EventState.RECEIVED, new DummyDTO("Peer discovered: " + peer.address()));
    }

    @Override
    public void onPeerLost(String peerId) {

    }

    @Override
    public void onMessageReceived(String peerId, byte[] payload) {
        StrategyDTO dto = synchronizationService.readMessage(peerId, payload);
        logEvent(EventState.RECEIVED, dto);
    }

    public void logEvent(EventState event, StrategyDTO dto) {
        observerAPI.publish(event, dto);
    }
}
