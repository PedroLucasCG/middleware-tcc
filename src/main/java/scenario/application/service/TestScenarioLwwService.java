package scenario.application.service;

import scenario.domain.ScenarioActionLww;
import scenario.infra.EventHandler;
import synchronization.domain.TransactionContent;
import synchronization.domain.TransactionRecord;
import transport.domain.NodeConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestScenarioLwwService implements TestScenarioService<ScenarioActionLww> {
    private final ScheduledExecutorService scheduledExecutorService;
    private final EventHandler eventHandler;

    public TestScenarioLwwService(ScheduledExecutorService scheduledExecutorService, EventHandler eventHandler) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.eventHandler = eventHandler;
    }

    @Override
    public List<ScenarioActionLww> loadFile(String pathToScenarioFile) {
        List<ScenarioActionLww> scenarioActionLwwList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(pathToScenarioFile))) {
            String scenarioLine = br.readLine();

            while (scenarioLine != null) {
                String[] partsFromScenarioLine = scenarioLine.split("\\|");
                scenarioActionLwwList.add(new ScenarioActionLww(
                        Integer.parseInt(partsFromScenarioLine[0].trim()),
                        partsFromScenarioLine[1].trim(),
                        UUID.nameUUIDFromBytes(
                                partsFromScenarioLine[2].trim().getBytes(StandardCharsets.UTF_8)),
                        partsFromScenarioLine[3].trim(),
                        Boolean.parseBoolean(partsFromScenarioLine[5].trim())
                ));
                scenarioLine = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return scenarioActionLwwList;
    }

    @Override
    public void run(String currentPeerName, List<ScenarioActionLww> scenarioActionCrdtList) {
        scenarioActionCrdtList.stream()
                .filter(action ->
                        action.sender().equals(currentPeerName)
                )
                .forEach(action ->
                        scheduledExecutorService.schedule(
                                () -> execute(currentPeerName, action),
                                action.atMs(),
                                TimeUnit.MILLISECONDS
                        )
                );

    }

    private void execute(String currentPeerName, ScenarioActionLww scenarioActionLww) {
        try {
            TransactionContent transactionContent =
                    new TransactionContent(
                            scenarioActionLww.transactionContentId(),
                            scenarioActionLww.transactionContent(),
                            scenarioActionLww.deleted()
                    );

            TransactionRecord transactionRecord =
                    new TransactionRecord(
                            transactionContent,
                            NodeConfig.defaults().nodeId()
                    );

            System.out.printf(
                    "[SCENARIO] peer=%s atMs=%d entity=%s "
                            + "message=%s deleted=%s%n",
                    currentPeerName,
                    scenarioActionLww.atMs(),
                    scenarioActionLww.transactionContentId(),
                    scenarioActionLww.transactionContent(),
                    scenarioActionLww.deleted()
            );

            eventHandler.send(transactionRecord);

        } catch (Exception exception) {
            System.err.printf(
                    "[SCENARIO ERROR] peer=%s entity=%s error=%s%n",
                    currentPeerName,
                    scenarioActionLww.transactionContentId(),
                    exception.getMessage()
            );

            exception.printStackTrace();
        }
    }
}
