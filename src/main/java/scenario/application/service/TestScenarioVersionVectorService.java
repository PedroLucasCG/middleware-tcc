package scenario.application.service;

import scenario.domain.ScenarioActionCrdt;
import scenario.domain.ScenarioActionLww;
import scenario.domain.ScenarioActionVersionVector;
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

public class TestScenarioVersionVectorService implements TestScenarioService<ScenarioActionVersionVector> {
    private final ScheduledExecutorService scheduledExecutorService;
    private final EventHandler eventHandler;

    public TestScenarioVersionVectorService(ScheduledExecutorService scheduledExecutorService, EventHandler eventHandler) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.eventHandler = eventHandler;
    }

    @Override
    public List<ScenarioActionVersionVector> loadFile(String pathToScenarioFile) {
        List<ScenarioActionVersionVector> scenarioActionCrdtList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(pathToScenarioFile))) {
            String scenarioLine = br.readLine();

            while (scenarioLine != null) {
                String[] partsFromScenarioLine = scenarioLine.split("\\|");
                scenarioActionCrdtList.add(new ScenarioActionVersionVector(
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
        return scenarioActionCrdtList;
    }

    @Override
    public void run(String currentPeerName, List<ScenarioActionVersionVector> scenarioActionVersionVectorList) {
        scenarioActionVersionVectorList.stream()
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

    private void execute(String currentPeerName, ScenarioActionVersionVector scenarioActionVersionVector) {
        try {
            TransactionContent transactionContent =
                    new TransactionContent(
                            scenarioActionVersionVector.transactionContentId(),
                            scenarioActionVersionVector.transactionContent(),
                            scenarioActionVersionVector.deleted()
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
                    scenarioActionVersionVector.atMs(),
                    scenarioActionVersionVector.transactionContentId(),
                    scenarioActionVersionVector.transactionContent(),
                    scenarioActionVersionVector.deleted()
            );

            eventHandler.send(transactionRecord);

        } catch (Exception exception) {
            System.err.printf(
                    "[SCENARIO ERROR] peer=%s entity=%s error=%s%n",
                    currentPeerName,
                    scenarioActionVersionVector.transactionContentId(),
                    exception.getMessage()
            );

            exception.printStackTrace();
        }
    }
}
