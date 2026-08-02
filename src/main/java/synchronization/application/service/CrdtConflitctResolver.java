package synchronization.application.service;

import synchronization.domain.TransactionRecord;


public class CrdtConflitctResolver implements ConflictResolver{
    @Override
    public TransactionRecord resolve(TransactionRecord local, TransactionRecord incoming) {
        local.mergeCrdtOperations(incoming);

        return new TransactionRecord(
                local.getMessage(),
                local.isDeleted(),
                local.getNodeId(),
                local.getAnnotationId(),
                local.crdtGetAll()
        );
    }
}
