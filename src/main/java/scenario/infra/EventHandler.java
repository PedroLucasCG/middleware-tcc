package scenario.infra;

import synchronization.domain.TransactionRecord;

public interface EventHandler {
    void send(TransactionRecord transactionRecord);
}
