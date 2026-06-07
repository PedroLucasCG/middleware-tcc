package transport.application;

import transport.domain.PeerInfo;

public interface TransportListener {
    void onPeerDiscovered(PeerInfo peer);

    void onPeerLost(String peerId);

    void onMessageReceived(String peerId, byte[] payload);
}
