package transport.aplication.controller;

import synchronization.application.listener.StrategyMiddleware;

public interface BroadcastController {
    void start(StrategyMiddleware strategyMiddleware);
    void stop();
    void broadcast(String record);
    void send(String peerId, String record);
}
