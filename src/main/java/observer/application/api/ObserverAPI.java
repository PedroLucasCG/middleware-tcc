package observer.application.api;

import observer.domain.WsEvent;
import synchronization.application.listener.StrategyDTO;

public interface ObserverAPI {
    void connect();
    void publish(WsEvent event, StrategyDTO strategyDTO);
    void disconnect();
}
