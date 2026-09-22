package scenario.domain;

import java.util.UUID;

public record ScenarioActionCrdt(
        Integer atMs,
        String sender,
        UUID transactionContentId,
        String transactionContent,
        Long operationStringIndex,
        boolean deleted) {
}
