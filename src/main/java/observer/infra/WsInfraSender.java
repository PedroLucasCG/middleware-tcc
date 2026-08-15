package observer.infra;

import observer.domain.WsEvent;
import synchronization.application.listener.StrategyDTO;

import java.net.http.WebSocket;

public interface WsInfraSender extends WebSocket.Listener {
    void connect();
    void publish(WsEvent wsEvent, StrategyDTO dto);
}
