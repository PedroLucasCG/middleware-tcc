package test_scenario.domain;

import synchronization.application.api.StrategyMiddleware;
import synchronization.domain.TransactionContent;
import synchronization.domain.TransactionRecord;
import transport.domain.NodeConfig;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScenarioRunner {
    private final StrategyMiddleware strategyMiddleware;
    private final ScheduledExecutorService scheduledExecutorService;

    public ScenarioRunner(StrategyMiddleware strategyMiddleware) {
        this.strategyMiddleware = strategyMiddleware;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void run(String currentPeerName, List<ScenarioSingleAction> scenarioSingleActionList) {
        scenarioSingleActionList.stream()
                .filter(scenarioSingleAction -> scenarioSingleAction.sender().equals(currentPeerName))
                .forEach(scenarioSingleAction -> scheduledExecutorService.schedule(
                        () -> execute(currentPeerName, scenarioSingleAction),
                        scenarioSingleAction.atMs(),
                        TimeUnit.MILLISECONDS
                ));
    }

    public void execute(String currentPeerName, ScenarioSingleAction scenarioSingleAction) {
        try {
            TransactionContent transactionContent = new TransactionContent(
                    scenarioSingleAction.transactionContentId(),
                    scenarioSingleAction.transactionContent(),
                    scenarioSingleAction.deleted(),
                    0L
            );

            TransactionRecord transactionRecord = new TransactionRecord(
                    transactionContent,
                    NodeConfig.defaults().nodeId()
            );

            System.out.printf(
                    "[SCENARIO] peer=%s atMs=%d entity=%s message=%s deleted=%s%n",
                    currentPeerName,
                    scenarioSingleAction.atMs(),
                    scenarioSingleAction.transactionContentId(),
                    scenarioSingleAction.transactionContent(),
                    scenarioSingleAction.deleted()
            );

            strategyMiddleware.createOrUpdate(transactionRecord);
        } catch (Exception exception) {
            System.err.printf(
                    "[SCENARIO ERROR] peer=%s entity=%s error=%s%n",
                    currentPeerName,
                    scenarioSingleAction.transactionContentId(),
                    exception.getMessage()
            );

            exception.printStackTrace();
        }
    }
}
