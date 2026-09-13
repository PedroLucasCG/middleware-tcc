package shared.utils.factory;

import synchronization.application.api.StrategyMiddleware;

public interface ContextFactory {
    StrategyMiddleware makeMiddleware();

    static ContextFactory getContextFactory(ContextType contextType) {
        return switch (contextType) {
            case DOCKER -> new DockerContextFactory();
            case MOBILE -> new MobileContextFactory();
        };
    }
}
