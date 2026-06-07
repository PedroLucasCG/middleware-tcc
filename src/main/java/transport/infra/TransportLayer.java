package transport.infra;

import transport.application.TransportListener;

public interface TransportLayer {
    void start();

    void stop();

    void broadcast(byte[] payload);

    void send(String peerId, byte[] payload);

    void setListener(TransportListener listener);
}
