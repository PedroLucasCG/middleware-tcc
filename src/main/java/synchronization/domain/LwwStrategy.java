package synchronization.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LwwStrategy implements SynchronizationStrategies {
    private final Map<String, TransactionRecord> records = new ConcurrentHashMap<>();

    @Override
    public void apply(TransactionRecord incoming) {
        records.merge(
                incoming.getId(),
                incoming,
                this::resolve
        );
    }

    private TransactionRecord resolve(TransactionRecord local, TransactionRecord incoming) {
        int timeComparison = incoming.getUpdatedAt().compareTo(local.getUpdatedAt());

        if (timeComparison > 0) {
            return incoming;
        }

        if (timeComparison < 0) {
            return local;
        }

        if (incoming.getNodeId().compareTo(local.getNodeId()) > 0) {
            return incoming;
        }

        return local;
    }

    public Optional<TransactionRecord> findById(String id) {
        return Optional.ofNullable(records.get(id));
    }

    public Map<String, TransactionRecord> snapshot() {
        return Map.copyOf(records);
    }
}
