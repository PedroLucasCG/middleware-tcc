package synchronization.application.service;

import synchronization.domain.CrdtInterpreter;
import synchronization.domain.TransactionRecord;


public class CrdtConflitctResolver implements ConflictResolver{
    private final CrdtInterpreter interpreter;

    public CrdtConflitctResolver() {
        this.interpreter = new CrdtInterpreter();
    }

    @Override
    public TransactionRecord resolve(
            TransactionRecord local,
            TransactionRecord incoming
    ) {
        local.mergeCrdtOperations(incoming);

        TransactionRecord interpreted =
                interpreter.interpretTransaction(local);

        boolean annotationDeleted =
                local.isDeleted() || incoming.isDeleted();

        return new TransactionRecord(
                interpreted.getMessage(),
                annotationDeleted,
                incoming.getNodeId(),
                local.getAnnotationId(),
                local.crdtGetAll()
        );
    }
}
