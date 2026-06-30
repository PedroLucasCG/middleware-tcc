package synchronization.domain;

public class LwwConflitctResolver implements ConflictResolver {

    public TransactionRecord resolve(TransactionRecord local, TransactionRecord incoming) {
        int timeComparison = incoming.getUpdatedAt().compareTo(local.getUpdatedAt());

        if (timeComparison > 0) {
            return incoming;
        }

        if (timeComparison < 0) {
            return local;
        }

        if (incoming.getNodeId().compareTo(local.getNodeId()) > 0) {
            return incoming;
        }

        return local;
    }
}
