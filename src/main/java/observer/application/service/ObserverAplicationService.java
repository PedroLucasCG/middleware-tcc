package observer.application.service;

import observer.domain.EventState;
import observer.infra.EventSender;
import synchronization.application.api.StrategyDTO;

public class ObserverAplicationService implements ObserverService {
    private final EventSender eventSender;

    public ObserverAplicationService(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    @Override
    public void connect() {
        eventSender.connect();
    }

    @Override
    public void publish(EventState event, StrategyDTO strategyDTO) {
        eventSender.publish(event, strategyDTO);
    }

    @Override
    public void disconnect() {
        System.out.println("disconnect not implemented yet");
    }
}
