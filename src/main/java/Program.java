import synchronization.application.LwwMiddleware;
import synchronization.application.StrategyMiddleware;
import synchronization.domain.Middleware;
import synchronization.domain.TransactionRecord;
import transport.infra.DockerBroadcastLayer;
import transport.infra.TransportLayer;

import java.time.Instant;
import java.util.*;

public class Program {
    public static void main(String[] args) throws Exception {
        TransportLayer transport = new DockerBroadcastLayer();
        StrategyMiddleware middleware = new LwwMiddleware(transport);
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
                        Middleware.getNodeId(),
                        false
                ));
            }
        }

    }
}
