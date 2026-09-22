package synchronization.application.api;

import scenario.application.api.TestScenarioAPI;
import synchronization.domain.TransactionRecord;
import transport.domain.PeerInfo;

public interface StrategyMiddleware {
    StrategyMiddleware start();
    void createOrUpdate(TransactionRecord transactionRecord);
    void delete(String id);
    void onMessage(String peerId, String message);
    void onPeerDiscovered(PeerInfo peer);
    void onPeerLost(String peerId);
    void onMessageReceived(String peerId, byte[] payload);
    void test();
    void setTestScenarioAPI(TestScenarioAPI testScenarioAPI);
}
