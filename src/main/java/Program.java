import shared.utils.factory.ContextFactory;
import shared.utils.factory.ContextType;
import synchronization.application.api.StrategyMiddleware;
import test_scenario.domain.ScenarioFileLoader;
import test_scenario.domain.ScenarioRunner;
import test_scenario.domain.ScenarioSingleAction;

import java.util.List;

public class Program {
    public static void main(String[] args) throws Exception {
        var middleware = ContextFactory
                .getContextFactory(ContextType.DOCKER)
                .makeMiddleware();

        middleware.start();
        executeTestScenario(middleware);
    }

    private static void executeTestScenario(StrategyMiddleware middleware) {
        String pathToScenarioFile = System.getenv("PATH_SCENARIO_FILE");
        String peerName = System.getenv("PEER_NAME");

        if (pathToScenarioFile == null) {
            throw new RuntimeException("PATH_SCENARIO_FILE environment variable is not set");
        }

        if (peerName == null) {
            throw new RuntimeException("PEER_NAME environment variable is not set");
        }

        List<ScenarioSingleAction> scenarioSingleActionList = ScenarioFileLoader.loadFile(pathToScenarioFile);

        var scenarioRunner = new ScenarioRunner(middleware);
        scenarioRunner.run(peerName, scenarioSingleActionList);
    }
}
