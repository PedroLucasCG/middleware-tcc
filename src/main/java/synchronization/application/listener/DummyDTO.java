package synchronization.application.listener;

import synchronization.domain.StrategyType;
import synchronization.domain.TransactionRecord;
import transport.domain.NodeConfig;

import java.time.Instant;
import java.util.UUID;

public class DummyDTO implements StrategyDTO{
    private StrategyType typeDTO;
    private Instant lastUpdate;
    private String message;

    public DummyDTO() {
        this.typeDTO = StrategyType.Dummy;
        this.message = "Peer connected to Observer" + NodeConfig.defaults().nodeId();
        this.lastUpdate = Instant.now();
    }

    @Override
    public TransactionRecord makeTransactionRecordFromDto() {
        return null;
    }
}
