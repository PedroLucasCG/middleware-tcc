package observer.application.service;

import observer.domain.WsEvent;
import observer.infra.WsInfraSender;
import synchronization.application.listener.StrategyDTO;

public class ObserverAplicationService implements ObserverService {
    private WsInfraSender wsInfraSender;

    public ObserverAplicationService(WsInfraSender wsInfraSender) {
        this.wsInfraSender = wsInfraSender;
    }

    @Override
    public void connect() {
        wsInfraSender.connect();
    }

    @Override
    public void publish(WsEvent event, StrategyDTO strategyDTO) {
        wsInfraSender.publish(event, strategyDTO);
    }

    @Override
    public void disconnect() {
        System.out.println("disconnect not implemented yet");
    }
}
