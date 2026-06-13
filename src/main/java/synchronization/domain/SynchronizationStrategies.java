package synchronization.domain;

public interface SynchronizationStrategies {
    void apply(TransactionRecord incoming);
}
