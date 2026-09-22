package shared.utils.factory;

import synchronization.application.api.StrategyMiddleware;
import synchronization.domain.StrategyType;

public interface ContextFactory {
    StrategyMiddleware makeMiddleware(StrategyType strategyType);

    static ContextFactory getContextFactory(ContextType contextType) {
        return switch (contextType) {
            case DOCKER -> new DockerContextFactory();
            case MOBILE -> new MobileContextFactory();
        };
    }
}
