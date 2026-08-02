package synchronization.domain;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CrdtInterpreter {
    public TransactionRecord interpretTransaction(TransactionRecord transactionRecord) {
        Map<UUID, Set<Crdt>> states = transactionRecord.crdtGetAll();

        return null;
    }
}
