package scenario.application.service;

import scenario.domain.ScenarioActionCrdt;
import scenario.infra.EventHandler;
import synchronization.domain.TransactionContent;
import synchronization.domain.TransactionRecord;
import transport.domain.NodeConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestScenarioCrdtService implements TestScenarioService<ScenarioActionCrdt> {

    private final ScheduledExecutorService scheduledExecutorService;
    private final EventHandler eventHandler;

    public TestScenarioCrdtService(
            EventHandler eventHandler
    ) {
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.eventHandler = eventHandler;
    }

    @Override
    public List<ScenarioActionCrdt> loadFile(String pathToScenarioFile) {
        List<ScenarioActionCrdt> scenarioActions = new ArrayList<>();

        Path scenarioPath = Path.of(pathToScenarioFile);

        try (BufferedReader reader = Files.newBufferedReader(
                scenarioPath,
                StandardCharsets.UTF_8
        )) {
            String scenarioLine;

            while ((scenarioLine = reader.readLine()) != null) {
                if (scenarioLine.isBlank()) {
                    continue;
                }

                String[] parts = scenarioLine.split("\\|");

                if (parts.length != 6) {
                    throw new IllegalArgumentException(
                            "Linha inválida no cenário: " + scenarioLine
                    );
                }

                ScenarioActionCrdt action = new ScenarioActionCrdt(
                        Integer.parseInt(parts[0].trim()),
                        parts[1].trim(),
                        UUID.nameUUIDFromBytes(
                                parts[2].trim().getBytes(StandardCharsets.UTF_8)
                        ),
                        parts[3].trim(),
                        Long.parseLong(parts[4].trim()),
                        Boolean.parseBoolean(parts[5].trim())
                );

                scenarioActions.add(action);
            }
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Erro ao carregar o arquivo de cenário: "
                            + pathToScenarioFile,
                    exception
            );
        }

        return scenarioActions;
    }

    @Override
    public void run(
            String currentPeerName,
            List<ScenarioActionCrdt> scenarioActionList
    ) {
        scenarioActionList.stream()
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

    private void execute(
            String currentPeerName,
            ScenarioActionCrdt scenarioActionCrdt
    ) {
        try {
            TransactionContent transactionContent =
                    new TransactionContent(
                            scenarioActionCrdt.transactionContentId(),
                            scenarioActionCrdt.transactionContent(),
                            scenarioActionCrdt.deleted(),
                            scenarioActionCrdt.operationStringIndex()
                    );

            TransactionRecord transactionRecord =
                    new TransactionRecord(
                            transactionContent,
                            NodeConfig.defaults().nodeId()
                    );

            System.out.printf(
                    "[SCENARIO] peer=%s atMs=%d entity=%s "
                            + "message=%s operationIndex=%d deleted=%s%n",
                    currentPeerName,
                    scenarioActionCrdt.atMs(),
                    scenarioActionCrdt.transactionContentId(),
                    scenarioActionCrdt.transactionContent(),
                    scenarioActionCrdt.operationStringIndex(),
                    scenarioActionCrdt.deleted()
            );

            eventHandler.send(transactionRecord);

        } catch (Exception exception) {
            System.err.printf(
                    "[SCENARIO ERROR] peer=%s entity=%s error=%s%n",
                    currentPeerName,
                    scenarioActionCrdt.transactionContentId(),
                    exception.getMessage()
            );

            exception.printStackTrace();
        }
    }
}