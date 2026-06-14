package synchronization.domain;

import transport.domain.PeerInfo;
import transport.infra.TransportLayer;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Middleware {
    private static final String nodeId = UUID.randomUUID().toString();
    private static final Map<String, PeerInfo> peers = new ConcurrentHashMap<>();
    private static final String GROUP = "230.0.0.0";
    private static final int PORT = 4446;

    public static String serialize(TransactionRecord record) {
        return String.join("|",
                record.getId(),
                record.getValue(),
                record.getUpdatedAt().toString(),
                record.getNodeId(),
                String.valueOf(record.isDeleted())
        );
    }

    public static TransactionRecord deserialize(String raw) {
        String[] parts = raw.split("\\|", -1);

        return new TransactionRecord(
                parts[0],
                parts[1],
                Instant.parse(parts[2]),
                parts[3],
                Boolean.parseBoolean(parts[4])
        );
    }

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
