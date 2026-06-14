package transport.infra;

import synchronization.application.listener.StrategyMiddleware;

public interface TransportLayer {
    void start();

    void stop();

    void broadcast(String transactionRecord);

    void send(String peerId, String transactionRecord);

    void setListener(StrategyMiddleware listener);
}
