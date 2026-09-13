package observer.application.api;

import observer.application.service.ObserverService;
import observer.domain.EventState;
import synchronization.application.api.StrategyDTO;

public class ObserverController implements ObserverAPI {
    private final ObserverService observerService;

    public ObserverController(ObserverService observerService) {
        this.observerService = observerService;
    }

    @Override
    public void connect() {
        observerService.connect();
    }

    @Override
    public void publish(EventState event, StrategyDTO strategyDTO) {
        observerService.publish(event, strategyDTO);
    }

    @Override
    public void disconnect() {
        observerService.disconnect();
    }
}
