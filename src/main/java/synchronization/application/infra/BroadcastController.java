package synchronization.application.infra;

import synchronization.application.api.StrategyMiddleware;

public interface BroadcastController {
    void start(StrategyMiddleware strategyMiddleware);
    void stop();
    void broadcast(String record);
    void send(String peerId, String record);
}