package scenario.application.api;

import scenario.application.service.TestScenarioService;
import scenario.domain.ScenarioActionCrdt;

import java.util.List;

public class TestScenarioRunner implements TestScenarioAPI {
    private final TestScenarioService testScenarioService;
    private final String filePath;
    private final String peerName;

    public TestScenarioRunner(TestScenarioService testScenarioService) {
        this.testScenarioService = testScenarioService;
        this.filePath = TestScenarioAPI.getScenarioFilePath();
        this.peerName = TestScenarioAPI.getPeerName();
    }

    @Override
    public void test() {
        List<ScenarioActionCrdt> testLines = testScenarioService.loadFile(filePath);
        testScenarioService.run(peerName, testLines);
    }
}
