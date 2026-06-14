package synchronization.domain;

import transport.domain.PeerInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Middleware {
    private static final String nodeId = UUID.randomUUID().toString();
    private static final Map<String, PeerInfo> peers = new ConcurrentHashMap<>();
    private static final String GROUP = "230.0.0.0";
    private static final int PORT = 4446;

    public static String getNodeId() {
        return nodeId;
    }

    public static Map<String, PeerInfo> getPeers() {
        return peers;
    }

    public static String getGroup() {
        return GROUP;
    }

    public static int getPort() {
        return PORT;
    }
}
