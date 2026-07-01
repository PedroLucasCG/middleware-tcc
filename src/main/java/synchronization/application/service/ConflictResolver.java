package synchronization.application.service;

import synchronization.domain.TransactionRecord;

public interface ConflictResolver {
    TransactionRecord resolve(TransactionRecord local, TransactionRecord incoming);
}
