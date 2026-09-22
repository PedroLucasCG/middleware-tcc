package scenario.application.service;

import scenario.domain.ScenarioActionCrdt;

import java.util.List;

public interface TestScenarioService<T> {
    List<T> loadFile(String pathToScenarioFile);

    void run(String currentPeerName, List<T> actions);
}
