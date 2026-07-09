package shared.utils;

import synchronization.domain.Crdt;

import java.util.*;
import java.util.stream.Collectors;

public class StrategyDTOStringParser {
    public static String mapVersionVectorToString(Map<UUID, Long> versionVector) {
        return versionVector.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(","));
    }

    public static Map<UUID, Long> stringToVersionVector(String value) {
        Map<UUID, Long> result = new HashMap<>();

        if (value == null || value.isBlank()) {
            return result;
        }

        for (String item : value.split(",")) {
            String[] pair = item.split("=", 2);

            if (pair.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid version-vector entry: " + item
                );
            }

            UUID nodeId = UUID.fromString(pair[0]);
            long counter = Long.parseLong(pair[1]);

            result.put(nodeId, counter);
        }

        return result;
    }

    public static String getCrdtSerializedOperations(Map<UUID, Set<Crdt>> operations) {
        return operations.entrySet()
                .stream()
                .map(entry -> {
                    String annotationId = entry.getKey().toString();

                    String crdts = entry.getValue()
                            .stream()
                            .map(Crdt::serialize)
                            .collect(Collectors.joining(","));

                    return annotationId + ":" + crdts;
                })
                .collect(Collectors.joining(";"));
    }

    public static Map<UUID, Set<Crdt>> crdtDeserializeOperations(String data) {
        Map<UUID, Set<Crdt>> operations = new HashMap<>();

        if (data == null || data.isBlank()) {
            return operations;
        }

        String[] annotations = data.split(";");

        for (String annotation : annotations) {

            String[] parts = annotation.split(":", 2);

            UUID annotationId = UUID.fromString(parts[0]);

            Set<Crdt> crdtSet = Arrays.stream(parts[1].split(";"))
                    .map(Crdt::deserialize)
                    .collect(Collectors.toSet());

            operations.put(annotationId, crdtSet);
        }

        return operations;
    }
}
