package synchronization.domain;

import java.util.*;
import java.util.stream.Collectors;

public class CrdtInterpreter {

    private static final Comparator<Crdt> OPERATION_ORDER =
            Comparator.comparingLong(Crdt::getCounter)
                    .thenComparing(Crdt::getNodeId)
                    .thenComparing(Crdt::getOperationId);

    public TransactionRecord interpretTransaction(
            TransactionRecord transactionRecord
    ) {
        String interpretedValue = interpretOperations(
                transactionRecord.crdGetOperationsByAnnotationId()
        );

        return new TransactionRecord(
                interpretedValue,
                transactionRecord.isDeleted(),
                transactionRecord.getNodeId(),
                transactionRecord.getAnnotationId(),
                transactionRecord.crdtGetAll()
        );
    }

    public String interpretOperations(Set<Crdt> operations) {
        if (operations == null || operations.isEmpty()) {
            return "";
        }

        Set<UUID> deletedInsertIds = findDeletedInsertIds(operations);

        Map<UUID, List<Crdt>> childrenByTarget =
                groupInsertionsByTarget(operations);

        childrenByTarget.values().forEach(
                children -> children.sort(OPERATION_ORDER)
        );

        StringBuilder result = new StringBuilder();
        Set<UUID> visited = new HashSet<>();

        appendOperations(
                null,
                childrenByTarget,
                deletedInsertIds,
                visited,
                result
        );

        return result.toString();
    }

    public List<Crdt> getVisibleInsertOperations(
            TransactionRecord transactionRecord
    ) {
        Set<Crdt> operations =
                transactionRecord.crdGetOperationsByAnnotationId();

        if (operations == null || operations.isEmpty()) {
            return Collections.emptyList();
        }

        Set<UUID> deletedInsertIds = findDeletedInsertIds(operations);

        Map<UUID, List<Crdt>> childrenByTarget =
                groupInsertionsByTarget(operations);

        childrenByTarget.values().forEach(
                children -> children.sort(OPERATION_ORDER)
        );

        List<Crdt> visibleOperations = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();

        collectVisibleOperations(
                null,
                childrenByTarget,
                deletedInsertIds,
                visited,
                visibleOperations
        );

        return visibleOperations;
    }

    private Set<UUID> findDeletedInsertIds(Set<Crdt> operations) {
        return operations.stream()
                .filter(Objects::nonNull)
                .filter(operation ->
                        operation.getType() == CrdtOperationType.DELETE
                )
                .map(Crdt::getTargetOperationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Map<UUID, List<Crdt>> groupInsertionsByTarget(
            Set<Crdt> operations
    ) {
        Map<UUID, List<Crdt>> childrenByTarget = new HashMap<>();

        operations.stream()
                .filter(Objects::nonNull)
                .filter(operation ->
                        operation.getType() == CrdtOperationType.INSERT
                )
                .forEach(operation -> {
                    UUID targetId = operation.getTargetOperationId();

                    childrenByTarget
                            .computeIfAbsent(
                                    targetId,
                                    ignored -> new ArrayList<>()
                            )
                            .add(operation);
                });

        return childrenByTarget;
    }

    private void appendOperations(
            UUID targetOperationId,
            Map<UUID, List<Crdt>> childrenByTarget,
            Set<UUID> deletedInsertIds,
            Set<UUID> visited,
            StringBuilder result
    ) {
        List<Crdt> children = childrenByTarget.getOrDefault(
                targetOperationId,
                Collections.emptyList()
        );

        for (Crdt operation : children) {
            UUID operationId = operation.getOperationId();

            if (operationId == null || !visited.add(operationId)) {
                continue;
            }

            if (!deletedInsertIds.contains(operationId)) {
                String value = operation.getContent();

                if (value != null) {
                    result.append(value);
                }
            }

            appendOperations(
                    operationId,
                    childrenByTarget,
                    deletedInsertIds,
                    visited,
                    result
            );
        }
    }

    private void collectVisibleOperations(
            UUID targetOperationId,
            Map<UUID, List<Crdt>> childrenByTarget,
            Set<UUID> deletedInsertIds,
            Set<UUID> visited,
            List<Crdt> result
    ) {
        List<Crdt> children = childrenByTarget.getOrDefault(
                targetOperationId,
                Collections.emptyList()
        );

        for (Crdt operation : children) {
            UUID operationId = operation.getOperationId();

            if (operationId == null || !visited.add(operationId)) {
                continue;
            }

            if (!deletedInsertIds.contains(operationId)) {
                result.add(operation);
            }

            collectVisibleOperations(
                    operationId,
                    childrenByTarget,
                    deletedInsertIds,
                    visited,
                    result
            );
        }
    }
}