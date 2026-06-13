import synchronization.application.LwwMiddleware;
import synchronization.application.StrategyMiddleware;
import transport.infra.DockerBroadcastLayer;
import transport.infra.TransportLayer;

public class Program {
    public static void main(String[] args) throws Exception {
        TransportLayer transport = new DockerBroadcastLayer();
        StrategyMiddleware lww = new LwwMiddleware(transport);
        lww.start();
    }
}
