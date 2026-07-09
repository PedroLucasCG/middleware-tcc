package synchronization.domain;

import java.util.*;

public class CrdtState {
    private final Map<UUID, Set<Crdt>> operations;

    public CrdtState(Map<UUID, Set<Crdt>> operations) {
        this.operations = operations;
    }

    public void addOperation(Crdt operation, UUID annotationId){
        operations
                .computeIfAbsent(
                        annotationId,
                        id -> new HashSet<>()
                )
                .add(operation);
    }

    public Set<Crdt> getOperations(UUID annotationId){
        return operations.getOrDefault(
                annotationId,
                Set.of()
        );
    }

    public Map<UUID, Set<Crdt>> getAll(){
        return operations;
    }

    public List<CrdtInfo> getCrdtInfo(){
        return operations
                .values()
                .stream()
                .flatMap(Set::stream)
                .map(Crdt::getCrdtInfo)
                .toList();
    }
}
