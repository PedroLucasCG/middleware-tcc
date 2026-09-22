package scenario.application.service;

import scenario.domain.ScenarioActionCrdt;
import scenario.infra.EventHandler;
import synchronization.application.service.CrdtService;
import synchronization.application.service.LwwService;
import synchronization.application.service.VersionVectorService;
import synchronization.domain.StrategyType;

import java.util.List;

public interface TestScenarioService<T> {
    List<T> loadFile(String pathToScenarioFile);

    void run(String currentPeerName, List<T> actions);

    static TestScenarioService getTestScenarioService(StrategyType strategyType, EventHandler eventHandler) {
        return switch(strategyType) {
            case CRDT -> new TestScenarioCrdtService(eventHandler);
            case LWW -> new TestScenarioLwwService(eventHandler);
            case VERSION_VECTOR -> new TestScenarioVersionVectorService(eventHandler);
            default -> throw new IllegalStateException("Unexpected value: " + strategyType);
        };
    }
}
