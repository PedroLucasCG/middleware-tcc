package observer.infra;

import observer.domain.EventState;
import synchronization.application.listener.StrategyDTO;

import java.net.http.WebSocket;

public interface EventSender extends WebSocket.Listener {
    void connect();
    void publish(EventState eventState, StrategyDTO dto);
}
