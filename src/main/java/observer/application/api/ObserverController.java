package observer.application.api;

import observer.application.service.ObserverService;
import observer.domain.WsEvent;
import synchronization.application.listener.StrategyDTO;

public class ObserverController implements ObserverAPI {
    private ObserverService observerService;

    public ObserverController(ObserverService observerService) {
        this.observerService = observerService;
    }

    @Override
    public void connect() {
        observerService.connect();
    }

    @Override
    public void publish(WsEvent event, StrategyDTO strategyDTO) {
        observerService.publish(event, strategyDTO);
    }

    @Override
    public void disconnect() {
        observerService.disconnect();
    }
}
