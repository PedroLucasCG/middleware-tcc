package synchronization.application;

import synchronization.domain.LwwStrategy;
import synchronization.domain.Middleware;
import transport.domain.PeerInfo;
import transport.infra.TransportLayer;

public class LwwMiddleware extends Middleware implements StrategyMiddleware {
    private final LwwStrategy store = new LwwStrategy();

    public LwwMiddleware(TransportLayer transportLayer) {
        super(transportLayer);
        super.getTransportLayer().setListener(this);
    }

    @Override
    public void start() {
        super.getTransportLayer().start();
    }

    @Override
    public void createOrUpdate(String id, String value) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void onMessage(String peerId, String message) {
        System.out.println("O par " + peerId + " mandou a mensagem: " + message);
    }

    @Override
    public void onPeerDiscovered(PeerInfo peer) {
        System.out.println("Par encontrado: " + peer.id());
    }

    @Override
    public void onPeerLost(String peerId) {

    }

    @Override
    public void onMessageReceived(String peerId, byte[] payload) {

    }
}
