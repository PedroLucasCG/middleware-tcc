package transport.aplication.service;

import synchronization.application.listener.StrategyMiddleware;

public interface BroadcastService {
    void start();
    void stop();
    void broadcast(String record);
    void send(String peerId, String record);
    void setListener(StrategyMiddleware listener);
}
