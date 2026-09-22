package synchronization.application.api;

import observer.application.api.ObserverAPI;
import observer.domain.EventState;
import scenario.application.api.TestScenarioAPI;
import synchronization.application.service.SynchronizationService;
import synchronization.domain.TransactionRecord;
import transport.domain.PeerInfo;

public class Controller implements StrategyMiddleware {
    private final SynchronizationService synchronizationService;
    private final ObserverAPI observerAPI;
    private TestScenarioAPI testScenarioAPI;

    public Controller(SynchronizationService synchronizationService, ObserverAPI observerAPI) {
        this.synchronizationService = synchronizationService;
        this.observerAPI = observerAPI;
    }

    @Override
    public void setTestScenarioAPI(TestScenarioAPI testScenarioAPI) {
        this.testScenarioAPI = testScenarioAPI;
    }

    @Override
    public Controller start() {
        synchronizationService.start(this);
        observerAPI.connect();
        return this;
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
        logEvent(EventState.CONNECTED, new DummyDTO("Peer discovered: " + peer.address()));
    }

    @Override
    public void onPeerLost(String peerId) {

    }

    @Override
    public void onMessageReceived(String peerId, byte[] payload) {
        StrategyDTO dto = synchronizationService.readMessage(peerId, payload);
        if (!dto.hasContent()) return;
        logEvent(EventState.RECEIVED, dto);
    }

    @Override
    public void test() {
        testScenarioAPI.test();
    }

    public void logEvent(EventState event, StrategyDTO dto) {
        observerAPI.publish(event, dto);
    }
}
