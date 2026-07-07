package synchronization.application.service;

import synchronization.domain.TransactionRecord;
import synchronization.domain.VectorRelation;

public class VersionVectorConflictResolver implements ConflictResolver {
    @Override
    public TransactionRecord resolve(TransactionRecord local, TransactionRecord incoming) {
        if (incoming == null || local == null) {
            throw new IllegalArgumentException("incoming or local cannot be null");
        }

        VectorRelation relation = local.versionVectorCompare(incoming);

        var result =  switch (relation) {
            case BEFORE -> incoming;
            case AFTER, EQUAL -> local;
            case CONCURRENT -> resolveConcurrent(local, incoming);
        };

        result.mergeReplicas(incoming);

        return result;
    }

    private TransactionRecord resolveConcurrent(TransactionRecord local, TransactionRecord incoming) {
        int timeComparison = local.getUpdatedAt().compareTo(incoming.getUpdatedAt());

        if (timeComparison > 0) {
            return local;
        }

        return incoming;
    }
}
