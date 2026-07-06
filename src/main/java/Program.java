import synchronization.application.listener.Listener;
import synchronization.application.listener.StrategyMiddleware;
import synchronization.application.service.LwwService;
import synchronization.application.service.SynchronizationService;
import synchronization.application.infra.BroadcastController;
import synchronization.application.service.VersionVectorService;
import synchronization.domain.Annotation;
import transport.aplication.controller.Controller;
import transport.aplication.service.DockerService;
import synchronization.domain.TransactionRecord;
import synchronization.infra.TransactionRecordHashMapStore;
import transport.domain.NodeConfig;

import java.util.*;

public class Program {
    public static void main(String[] args) throws Exception {
        BroadcastController controller = new Controller(new DockerService());
        SynchronizationService service = new VersionVectorService(controller, new TransactionRecordHashMapStore());
        StrategyMiddleware middleware = new Listener(service);
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
                        new Annotation(message, UUID.fromString(id)),
                        NodeConfig.defaults().nodeId()
                ));
            }
        }

    }
}
