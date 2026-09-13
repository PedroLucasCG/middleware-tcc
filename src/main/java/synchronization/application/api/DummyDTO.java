package synchronization.application.api;

import synchronization.domain.StrategyType;
import synchronization.domain.TransactionRecord;
import transport.domain.NodeConfig;

import java.time.Instant;

public class DummyDTO implements StrategyDTO{
    private StrategyType typeDTO;
    private Instant lastUpdate;
    private String message;

    public DummyDTO() {
        this.typeDTO = StrategyType.Dummy;
        this.message = "Peer " + NodeConfig.defaults().peerName() + " connected to Observer";
        this.lastUpdate = Instant.now();
    }

    public DummyDTO(String message) {
        this.typeDTO = StrategyType.Dummy;
        this.message = message;
        this.lastUpdate = Instant.now();
    }

    @Override
    public String toString() {
        return String.join("|", typeDTO.toString(), message, lastUpdate.toString());
    }

    @Override
    public TransactionRecord makeTransactionRecordFromDto() {
        return null;
    }

    @Override
    public Boolean hasContent() {
        return this.message != null && !this.message.isEmpty();
    }
}
