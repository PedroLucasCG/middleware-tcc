package shared.utils;

import synchronization.application.listener.StrategyDTO;
import synchronization.domain.TransactionRecord;

public class ByteMessageHandler {
    public static String serialize(StrategyDTO dto) {
        return dto.toString();
    }

    public static TransactionRecord deserialize(String raw) {
        String[] parts = raw.split("\\|", -1);
        StrategyDTO strategyDTO = StrategyDTO.makeStrategyDTO(parts);
        return strategyDTO.makeTransactionRecordFromDto();
    }
}
