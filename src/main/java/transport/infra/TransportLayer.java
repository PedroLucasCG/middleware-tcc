package transport.infra;

import synchronization.application.listener.StrategyMiddleware;

import java.net.DatagramPacket;

public interface TransportLayer {
    void start();

    void stop();

    void setListener(StrategyMiddleware listener);

    void sendMulticast(String message);

    void listen();
}
