import shared.utils.factory.ContextFactory;
import shared.utils.factory.ContextType;
import synchronization.application.api.StrategyMiddleware;
import scenario.domain.ScenarioActionCrdt;

import java.util.List;

public class Program {
    public static void main(String[] args) throws Exception {
       ContextFactory
            .getContextFactory(ContextType.DOCKER)
            .makeMiddleware()
            .start();
    }

}
