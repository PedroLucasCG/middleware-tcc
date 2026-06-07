package transport.application;

import transport.domain.PeerInfo;

public class TransportListenerDocker implements TransportListener {
    @Override
    public void onPeerDiscovered(PeerInfo peer) {
        System.out.println("Discovered: " + peer);
    }

    @Override
    public void onPeerLost(String peerId) {
        System.out.println("Lost: " + peerId);
    }

    @Override
    public void onMessageReceived(String peerId, byte[] payload) {
        System.out.println("Message from " + peerId + ": " + new String(payload));
    }
}
