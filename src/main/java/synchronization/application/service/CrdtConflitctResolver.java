package synchronization.application.service;

import synchronization.domain.TransactionRecord;

public class CrdtConflitctResolver implements ConflictResolver{
    @Override
    public TransactionRecord resolve(TransactionRecord local, TransactionRecord incoming) {
        return null;
    }
}
