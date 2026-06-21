import synchronization.application.listener.Listener;
import synchronization.application.listener.StrategyMiddleware;
import synchronization.application.service.LwwService;
import synchronization.application.service.SynchronizationService;
import synchronization.application.infra.BroadcastController;
import transport.aplication.controller.Controller;
import transport.aplication.service.DockerService;
import synchronization.domain.TransactionRecord;
import synchronization.infra.ConcurrentHashMapStore;
import transport.domain.NodeConfig;

import java.time.Instant;
import java.util.*;

public class Program {
    public static void main(String[] args) throws Exception {
        BroadcastController controller = new Controller(new DockerService());
        SynchronizationService service = new LwwService(controller, new ConcurrentHashMapStore());
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
                int randomIndex = random.nextInt(messages.length);
                String message = messages[randomIndex][1];
                String id =  messages[randomIndex][0];
                middleware.createOrUpdate(new TransactionRecord(
                        id,
                        message,
                        Instant.now(),
                        NodeConfig.defaults().nodeId(),
                        false
                ));
            }
        }

    }
}
