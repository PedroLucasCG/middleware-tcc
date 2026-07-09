package synchronization.application.listener;

import synchronization.domain.StrategyType;
import synchronization.domain.TransactionRecord;

public interface StrategyDTO {
    String toString();
    TransactionRecord makeTransactionRecordFromDto();

    static StrategyDTO makeStrategyDTO(String[] parts) {
        if (parts.length == 0 || parts[0].isBlank()) {
            throw new IllegalArgumentException(
                    "Serialized message does not contain a strategy type"
            );
        }

        StrategyType strategyType;

        try {
            strategyType = StrategyType.valueOf(parts[0]);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unknown strategy type: " + parts[0],
                    exception
            );
        }

        return switch (strategyType) {
            case LWW -> new LwwDTO(parts);

            case VERSION_VECTOR -> new VersionVectorDTO(parts);

            case CRDT -> new CrdtDTO(parts);
        };
    }
}
