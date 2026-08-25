import shared.utils.factory.ContextFactory;
import shared.utils.factory.ContextType;

public class Program {
    public static void main(String[] args) throws Exception {
        ContextFactory
                .getContextFactory(ContextType.DOCKER)
                .makeMiddleware()
                .start();
    }
}
