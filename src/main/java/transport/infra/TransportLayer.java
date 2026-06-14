package transport.infra;

import synchronization.application.StrategyMiddleware;
import synchronization.domain.TransactionRecord;

public interface TransportLayer {
    void start();

    void stop();

    void broadcast(String transactionRecord);

    void send(String peerId, String transactionRecord);

    void setListener(StrategyMiddleware listener);
}
