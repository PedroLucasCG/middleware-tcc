import observer.application.api.ObserverAPI;
import observer.application.api.ObserverController;
import observer.application.service.ObserverAplicationService;
import observer.application.service.ObserverService;
import observer.domain.Observer;
import observer.infra.EventSender;
import observer.infra.WsEventSender;
import synchronization.application.listener.Listener;
import synchronization.application.listener.StrategyMiddleware;
import synchronization.application.service.CrdtService;
import synchronization.application.service.SynchronizationService;
import synchronization.application.infra.BroadcastController;
import synchronization.domain.TransactionContent;
import transport.aplication.controller.Controller;
import transport.aplication.service.DockerService;
import synchronization.domain.TransactionRecord;
import synchronization.infra.TransactionRecordHashMapStore;
import transport.domain.NodeConfig;

import java.util.*;

public class Program {
    public static void main(String[] args) throws Exception {
        BroadcastController controller = new Controller(new DockerService());
        SynchronizationService service = new CrdtService(controller, new TransactionRecordHashMapStore());

        EventSender eventSender = new WsEventSender(new Observer());
        ObserverService observerService = new ObserverAplicationService(eventSender);
        ObserverAPI observerAPI = new ObserverController(observerService);

        StrategyMiddleware middleware = new Listener(service, observerAPI);
        middleware.start();

        //teste
        boolean sender = Boolean.parseBoolean(
                System.getenv().getOrDefault("SENDER", "false")
        );

        UUID sharedId = UUID.randomUUID();
        String[][] messages = {
                {sharedId.toString(), "oi"},
                {UUID.randomUUID().toString(), "olá"},
                {sharedId.toString(), "oi de volta"},
        };

        Random random = new Random();

        if (sender) {
            for(int i = 0; i < messages.length; i++){
                Thread.sleep(5000);
                String message = messages[i][1];
                String id =  messages[i][0];
                middleware.createOrUpdate(new TransactionRecord(
                        new TransactionContent(UUID.fromString(id), message, false, 0L),
                        NodeConfig.defaults().nodeId()
                ));
            }
        }

    }
}
