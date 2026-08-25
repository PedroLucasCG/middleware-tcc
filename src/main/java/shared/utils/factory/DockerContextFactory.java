package shared.utils.factory;

import observer.application.api.ObserverAPI;
import observer.application.api.ObserverController;
import observer.application.service.ObserverAplicationService;
import observer.application.service.ObserverService;
import observer.domain.Observer;
import observer.infra.EventSender;
import observer.infra.WsEventSender;
import synchronization.application.infra.BroadcastController;
import synchronization.application.listener.Listener;
import synchronization.application.listener.StrategyMiddleware;
import synchronization.application.service.CrdtService;
import synchronization.application.service.SynchronizationService;
import synchronization.infra.TransactionRecordHashMapStore;
import transport.aplication.controller.Controller;
import transport.aplication.service.DockerService;

public class DockerContextFactory implements ContextFactory {
    @Override
    public StrategyMiddleware makeMiddleware() {
        BroadcastController controller = new Controller(new DockerService());
        SynchronizationService service = new CrdtService(controller, new TransactionRecordHashMapStore());

        EventSender eventSender = new WsEventSender(new Observer());
        ObserverService observerService = new ObserverAplicationService(eventSender);
        ObserverAPI observerAPI = new ObserverController(observerService);

        StrategyMiddleware middleware = new Listener(service, observerAPI);
        return middleware;
    }
}
