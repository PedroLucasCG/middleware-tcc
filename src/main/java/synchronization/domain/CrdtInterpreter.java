package synchronization.domain;

import java.util.*;
import java.util.stream.Collectors;

public class CrdtInterpreter {

    /*
     * Provides the same deterministic ordering on every replica when
     * multiple INSERT operations reference the same predecessor.
     */
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

    /**
     * Reconstructs the visible text represented by a set of CRDT operations.
     */
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

    /**
     * Returns the INSERT operation corresponding to every currently visible
     * character or fragment, in interpreted string order.
     *
     * This is useful when an operationStringIndex must be converted into an
     * operation ID for a DELETE operation.
     */
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

    /**
     * Each DELETE points to the operationId of an earlier INSERT.
     */
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

    /**
     * Groups every INSERT by the INSERT that precedes it.
     *
     * A null target means insertion at the beginning of the annotation.
     */
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

    /**
     * Traverses the operation graph and appends only visible insertions.
     */
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

            /*
             * Avoid duplicate processing and protect against malformed
             * operation cycles.
             */
            if (operationId == null || !visited.add(operationId)) {
                continue;
            }

            /*
             * A deleted insertion stays in the graph but its value is not
             * included in the visible result.
             */
            if (!deletedInsertIds.contains(operationId)) {
                String value = operation.getContent();

                if (value != null) {
                    result.append(value);
                }
            }

            /*
             * Always visit children, including children of deleted inserts.
             * The deleted insert acts as a structural tombstone.
             */
            appendOperations(
                    operationId,
                    childrenByTarget,
                    deletedInsertIds,
                    visited,
                    result
            );
        }
    }

    /**
     * Performs the same traversal but returns the visible INSERT objects
     * instead of constructing a string.
     */
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