package transport.infra;

import synchronization.application.listener.StrategyMiddleware;
import synchronization.domain.Middleware;
import transport.domain.PeerInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.Base64;

public class DockerBroadcastLayer implements TransportLayer {
    private static final String GROUP = Middleware.getGroup();
    private static final int PORT = Middleware.getPort();

    private final String peerId = Middleware.getNodeId();
    private final Map<String, PeerInfo> peers = Middleware.getPeers();

    private volatile boolean running;
    private StrategyMiddleware listener;

    @Override
    public void start() {
        running = true;

        new Thread(this::listen, "docker-broadcast-listener").start();
        new Thread(this::announceLoop, "docker-broadcast-announcer").start();
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void broadcast(String record) {
        byte[] payload = record.getBytes(StandardCharsets.UTF_8);
        sendMulticast("MSG " + peerId + " " + encode(payload));
    }

    @Override
    public void send(String peerId, String record) {
        broadcast(record);
    }

    @Override
    public void setListener(StrategyMiddleware listener) {
        this.listener = listener;
    }

    private void announceLoop() {
        while (running) {
            sendMulticast("HELLO " + peerId + " " + Instant.now());

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    private void listen() {
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            InetAddress group = InetAddress.getByName(GROUP);
            socket.joinGroup(group);

            byte[] buffer = new byte[4096];

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String raw = new String(
                        packet.getData(),
                        0,
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );

                handlePacket(packet, raw);
            }

        } catch (Exception e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    private void handlePacket(DatagramPacket packet, String raw) {
        String[] parts = raw.split(" ", 4);

        if (parts.length < 2) {
            return;
        }

        String type = parts[0];
        String senderId = parts[1];

        if (senderId.equals(peerId)) {
            return;
        }

        String ip = packet.getAddress().getHostAddress();

        switch (type) {
            case "HELLO" -> handleHello(senderId, ip, parts);
            case "MSG" -> handleMessage(senderId, parts);
            default -> {
            }
        }
    }

    private void handleHello(String senderId, String ip, String[] parts) {
        if (parts.length < 3) {
            return;
        }

        Instant lastSeen = Instant.parse(parts[2]);

        PeerInfo peer = new PeerInfo(
                UUID.fromString(senderId),
                ip,
                lastSeen
        );

        if (listener != null && !peers.containsKey(senderId)) {
            listener.onPeerDiscovered(peer);
        }

        peers.put(senderId, peer);
    }

    private void handleMessage(String senderId, String[] parts) {
        if (parts.length < 3) {
            return;
        }

        byte[] payload = decode(parts[2]);

        if (listener != null) {
            listener.onMessageReceived(senderId, payload);
        }
    }

    private void sendMulticast(String message) {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getByName(GROUP),
                    PORT
            );

            socket.send(packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String encode(byte[] payload) {
        return Base64.getEncoder().encodeToString(payload);
    }

    private byte[] decode(String payload) {
        return Base64.getDecoder().decode(payload);
    }
}
