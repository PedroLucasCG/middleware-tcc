package transport.domain;

import java.util.UUID;

public record NodeConfig(UUID nodeId, String group, int port, String peerName) {

    private static final String DEFAULT_GROUP = "230.0.0.0";
    private static final int DEFAULT_PORT = 4446;
    private static final UUID NODE_ID = UUID.randomUUID();
    private static final String PEER_NAME = System.getenv("PEER_NAME");

    public static NodeConfig defaults() {
        return new NodeConfig(NODE_ID, DEFAULT_GROUP, DEFAULT_PORT, PEER_NAME);
    }
}