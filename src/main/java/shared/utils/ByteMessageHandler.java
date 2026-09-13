package shared.utils;

import synchronization.application.api.StrategyDTO;

public class ByteMessageHandler {
    public static String serialize(StrategyDTO dto) {
        return dto.toString();
    }

    public static StrategyDTO deserialize(String raw) {
        String[] parts = raw.split("\\|", -1);
        StrategyDTO strategyDTO = StrategyDTO.makeStrategyDTO(parts);
        return strategyDTO;
    }
}
