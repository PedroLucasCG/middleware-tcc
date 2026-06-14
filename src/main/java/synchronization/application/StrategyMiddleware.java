package synchronization.application;

import synchronization.domain.TransactionRecord;
import transport.domain.PeerInfo;

public interface StrategyMiddleware {
    void start();
    void createOrUpdate(TransactionRecord transactionRecord);
    void delete(String id);
    void onMessage(String peerId, String message);
    void onPeerDiscovered(PeerInfo peer);
    void onPeerLost(String peerId);
    void onMessageReceived(String peerId, byte[] payload);
}
