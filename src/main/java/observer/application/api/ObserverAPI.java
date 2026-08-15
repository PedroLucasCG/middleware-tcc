package observer.application.api;

import observer.domain.EventState;
import synchronization.application.listener.StrategyDTO;

public interface ObserverAPI {
    void connect();
    void publish(EventState event, StrategyDTO strategyDTO);
    void disconnect();
}
