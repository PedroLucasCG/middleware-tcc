package observer.infra;

import observer.domain.EventState;
import synchronization.application.listener.StrategyDTO;

import java.net.http.WebSocket;

public interface EventSender {
    void connect();
    void publish(EventState eventState, StrategyDTO dto);
}
