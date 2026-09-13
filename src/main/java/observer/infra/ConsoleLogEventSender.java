package observer.infra;

import observer.domain.EventState;
import synchronization.application.api.StrategyDTO;

public class ConsoleLogEventSender implements EventSender {
    @Override
    public void connect() {
        System.out.println("Peer is online");
    }

    @Override
    public void publish(EventState eventState, StrategyDTO dto) {
        System.out.println(eventState.toString() + " " + dto.toString());
    }
}
