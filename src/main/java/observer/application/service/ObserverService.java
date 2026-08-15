package observer.application.service;

import observer.domain.WsEvent;
import synchronization.application.listener.StrategyDTO;

public interface ObserverService {
    void connect();
    void publish(WsEvent event, StrategyDTO strategyDTO);
    void disconnect();
}
