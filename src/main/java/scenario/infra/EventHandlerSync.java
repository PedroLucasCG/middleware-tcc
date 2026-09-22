package scenario.infra;

import synchronization.application.api.StrategyMiddleware;
import synchronization.domain.TransactionRecord;

public class EventHandlerSync implements EventHandler {
    private final StrategyMiddleware strategyMiddleware;

    public EventHandlerSync(StrategyMiddleware strategyMiddleware) {
        this.strategyMiddleware = strategyMiddleware;
    }

    @Override
    public void send(TransactionRecord transactionRecord) {
        strategyMiddleware.createOrUpdate(transactionRecord);
    }
}
