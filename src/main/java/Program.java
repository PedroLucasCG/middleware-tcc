import shared.utils.factory.ContextFactory;
import shared.utils.factory.ContextType;
import synchronization.domain.StrategyType;

public class Program {
    public static void main(String[] args) throws Exception {
       ContextFactory
               .getContextFactory(ContextType.DOCKER)
               .makeMiddleware(StrategyType.CRDT)
               .start()
               .test();
    }
}
