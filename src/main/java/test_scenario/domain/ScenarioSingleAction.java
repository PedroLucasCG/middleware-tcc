package test_scenario.domain;

import java.util.UUID;

public record ScenarioSingleAction(
        Integer atMs,
        String sender,
        UUID transactionContentId,
        String transactionContent,
        boolean deleted) {
}
