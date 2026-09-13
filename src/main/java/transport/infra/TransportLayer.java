package transport.infra;

import synchronization.application.api.StrategyMiddleware;

public interface TransportLayer {
    void start();

    void stop();

    void setListener(StrategyMiddleware listener);

    void sendMulticast(String message);

    void listen();
}
