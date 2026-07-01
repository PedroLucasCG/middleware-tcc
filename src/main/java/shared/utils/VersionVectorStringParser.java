package shared.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class VersionVectorStringParser {
    public static String mapToString(Map<UUID, Long> versionVector) {
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
}
