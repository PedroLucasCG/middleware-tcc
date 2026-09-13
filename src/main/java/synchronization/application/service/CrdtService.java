package synchronization.application.service;

import shared.utils.ByteMessageHandler;
import synchronization.application.infra.BroadcastController;
import synchronization.application.infra.RecordStore;
import synchronization.application.api.CrdtDTO;
import synchronization.application.api.StrategyDTO;
import synchronization.application.api.StrategyMiddleware;
import synchronization.domain.CrdtOperationType;
import synchronization.domain.TransactionRecord;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class CrdtService implements SynchronizationService {
    private final BroadcastController controller;
    private final RecordStore recordStore;

    public CrdtService(BroadcastController controller, RecordStore recordStore) {
        this.controller = controller;
        this.recordStore = recordStore;
    }

    @Override
    public StrategyDTO upsertMessage(TransactionRecord transactionRecord) {
        transactionRecord.crdtAddOperationForAnnotation(CrdtOperationType.INSERT);
        var data = new CrdtDTO(transactionRecord);
        controller.broadcast(
                ByteMessageHandler.serialize(data)
        );
        recordStore.addTransactionRecord(transactionRecord);
        return data;
    }

    @Override
    public StrategyDTO readMessage(String peerId, byte[] payload) {
        String value = new String(payload, StandardCharsets.UTF_8);
        StrategyDTO incomingDto = ByteMessageHandler.deserialize(value);
        TransactionRecord incomingRecord = incomingDto.makeTransactionRecordFromDto();
        recordStore.mergeIncomingRecord(incomingRecord, new CrdtConflitctResolver());
        return incomingDto;
    }

    @Override
    public void start(StrategyMiddleware listener) {
        controller.start(listener);
    }

    private Map<UUID, TransactionRecord> snapshot() {
        return Map.copyOf(recordStore.getAllTransactionRecords());
    }
}
