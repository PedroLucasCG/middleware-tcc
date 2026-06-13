package transport.infra;

import synchronization.application.StrategyMiddleware;

public interface TransportLayer {
    void start();

    void stop();

    void broadcast(byte[] payload);

    void send(String peerId, byte[] payload);

    void setListener(StrategyMiddleware listener);
}
