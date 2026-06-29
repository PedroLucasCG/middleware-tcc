package transport.aplication.service;

import synchronization.application.listener.StrategyMiddleware;
import transport.domain.NodeConfig;
import transport.infra.DockerBroadcastLayer;
import transport.infra.TransportLayer;
import transport.infra.WireMessageCodec;

import java.nio.charset.StandardCharsets;

public class DockerService implements BroadcastService {

    private final NodeConfig config;
    private final TransportLayer transportLayer;
    private final WireMessageCodec codec;

    public DockerService() {
        this(NodeConfig.defaults(), new WireMessageCodec());
    }

    private DockerService(NodeConfig config, WireMessageCodec codec) {
        this(config, new DockerBroadcastLayer(config), codec);
    }

    public DockerService(NodeConfig config, TransportLayer transportLayer, WireMessageCodec codec) {
        this.config = config;
        this.transportLayer = transportLayer;
        this.codec = codec;
    }

    @Override
    public void start() {
        transportLayer.start();
    }

    @Override
    public void stop() {
        transportLayer.stop();
    }

    @Override
    public void broadcast(String record) {
        byte[] payload = record.getBytes(StandardCharsets.UTF_8);
        transportLayer.sendMulticast(codec.encodeMsg(config.nodeId().toString(), payload));
    }

    @Override
    public void send(String peerId, String record) {
        this.broadcast(record);
    }

    @Override
    public void setListener(StrategyMiddleware listener) {
        transportLayer.setListener(listener);
    }
}