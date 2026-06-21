package transport.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeerStore {

    private final Map<String, PeerInfo> peers;

    public PeerStore() {
        this(new ConcurrentHashMap<>());
    }

    public PeerStore(Map<String, PeerInfo> peers) {
        this.peers = peers;
    }

    public boolean isNew(String peerId) {
        return !peers.containsKey(peerId);
    }

    public void upsert(String peerId, PeerInfo info) {
        peers.put(peerId, info);
    }

    public PeerInfo get(String peerId) {
        return peers.get(peerId);
    }

    public Map<String, PeerInfo> snapshot() {
        return Map.copyOf(peers);
    }
}