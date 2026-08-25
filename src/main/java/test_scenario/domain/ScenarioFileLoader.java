package test_scenario.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScenarioFileLoader {
    public static List<ScenarioSingleAction> loadFile(String pathToScenarioFile) {
        List<ScenarioSingleAction> scenarioSingleActionList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(pathToScenarioFile))) {
            String scenarioLine = br.readLine();

            while (scenarioLine != null) {
                String[] partsFromScenarioLine = scenarioLine.split("\\|");
                scenarioSingleActionList.add(new ScenarioSingleAction(
                        Integer.parseInt(partsFromScenarioLine[0].trim()),
                        partsFromScenarioLine[1].trim(),
                        UUID.nameUUIDFromBytes(
                                partsFromScenarioLine[2].trim().getBytes(StandardCharsets.UTF_8)),
                        partsFromScenarioLine[3].trim(),
                        Boolean.parseBoolean(partsFromScenarioLine[4].trim())
                ));
                scenarioLine = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return scenarioSingleActionList;
    }
}
