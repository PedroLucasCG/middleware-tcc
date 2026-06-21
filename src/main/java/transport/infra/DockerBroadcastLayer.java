package transport.infra;

import synchronization.application.listener.StrategyMiddleware;
import transport.domain.NodeConfig;
import transport.domain.PeerInfo;
import transport.domain.PeerRegistry;
import transport.domain.WireMessage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DockerBroadcastLayer implements TransportLayer {
    private static final long ANNOUNCE_INTERVAL_MILLIS = 3000;

    private final NodeConfig config;
    private final WireMessageCodec codec;
    private final PeerRegistry peerRegistry;
    private final PeerAnnouncer announcer;

    private volatile boolean running;
    private StrategyMiddleware listener;

    public DockerBroadcastLayer(NodeConfig config) {
        this(config, new WireMessageCodec(), new PeerRegistry());
    }

    public DockerBroadcastLayer(NodeConfig config, WireMessageCodec codec, PeerRegistry peerRegistry) {
        this.config = config;
        this.codec = codec;
        this.peerRegistry = peerRegistry;
        this.announcer = new PeerAnnouncer(
                () -> codec.encodeHello(config.nodeId()),
                this::sendMulticast,
                ANNOUNCE_INTERVAL_MILLIS
        );
    }

    @Override
    public void start() {
        running = true;
        new Thread(this::listen, "docker-broadcast-listener").start();
        new Thread(announcer::run, "docker-broadcast-announcer").start();
    }

    @Override
    public void stop() {
        running = false;
        announcer.stop();
    }

    @Override
    public void setListener(StrategyMiddleware listener) {
        this.listener = listener;
    }

    @Override
    public void listen() {
        try (MulticastSocket socket = new MulticastSocket(config.port())) {
            InetAddress group = InetAddress.getByName(config.group());
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

                dispatch(packet, raw);
            }

        } catch (Exception e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(DatagramPacket packet, String raw) {
        codec.decode(raw).ifPresent(message -> {
            String senderId = senderIdOf(message);

            if (senderId.equals(config.nodeId())) {
                return;
            }

            String ip = packet.getAddress().getHostAddress();

            if (message instanceof WireMessage.Hello hello) {
                onHello(hello, ip);
            } else if (message instanceof WireMessage.Msg msg) {
                onMessage(msg);
            }
        });
    }

    private String senderIdOf(WireMessage message) {
        if (message instanceof WireMessage.Hello hello) {
            return hello.senderId();
        } else if (message instanceof WireMessage.Msg msg) {
            return msg.senderId();
        }
        throw new IllegalStateException("Unknown WireMessage type: " + message.getClass());
    }

    private void onHello(WireMessage.Hello hello, String ip) {
        PeerInfo peer = new PeerInfo(
                UUID.fromString(hello.senderId()),
                ip,
                hello.timestamp()
        );

        if (listener != null && peerRegistry.isNew(hello.senderId())) {
            listener.onPeerDiscovered(peer);
        }

        peerRegistry.upsert(hello.senderId(), peer);
    }

    private void onMessage(WireMessage.Msg msg) {
        if (listener != null) {
            listener.onMessageReceived(msg.senderId(), msg.payload());
        }
    }

    @Override
    public void sendMulticast(String message) {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getByName(config.group()),
                    config.port()
            );

            socket.send(packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}