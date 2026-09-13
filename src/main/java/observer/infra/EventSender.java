package observer.infra;

import observer.domain.EventState;
import synchronization.application.api.StrategyDTO;

public interface EventSender {
    void connect();
    void publish(EventState eventState, StrategyDTO dto);
}
