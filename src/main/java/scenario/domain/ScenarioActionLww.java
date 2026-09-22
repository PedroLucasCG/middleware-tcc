package scenario.domain;

import java.util.UUID;

public record ScenarioActionLww(
        Integer atMs,
        String sender,
        UUID transactionContentId,
        String transactionContent,
        boolean deleted) {
}
