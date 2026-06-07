import transport.application.TransportListenerDocker;
import transport.infra.DockerBroadcastLayer;
import transport.infra.TransportLayer;

public class Program {
    public static void main(String[] args) throws Exception {
        TransportLayer transport = new DockerBroadcastLayer();
        transport.setListener(new TransportListenerDocker());
        transport.start();
    }
}
