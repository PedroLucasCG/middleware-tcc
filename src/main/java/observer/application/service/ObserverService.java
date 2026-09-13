package observer.application.service;

import observer.domain.EventState;
import synchronization.application.api.StrategyDTO;

public interface ObserverService {
    void connect();
    void publish(EventState event, StrategyDTO strategyDTO);
    void disconnect();
}
