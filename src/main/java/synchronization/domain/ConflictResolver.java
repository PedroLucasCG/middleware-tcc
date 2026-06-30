package synchronization.domain;

public interface ConflictResolver {
    TransactionRecord resolve(TransactionRecord local, TransactionRecord incoming);
}
