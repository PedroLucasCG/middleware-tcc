package shared.utils;

import synchronization.domain.TransactionRecord;

import java.time.Instant;

public class ByteMessageHandler {
    public static String serialize(TransactionRecord record) {
        return String.join("|",
                record.getId(),
                record.getValue(),
                record.getUpdatedAt().toString(),
                record.getNodeId(),
                String.valueOf(record.isDeleted())
        );
    }

    public static TransactionRecord deserialize(String raw) {
        String[] parts = raw.split("\\|", -1);

        return new TransactionRecord(
                parts[0],
                parts[1],
                Instant.parse(parts[2]),
                parts[3],
                Boolean.parseBoolean(parts[4])
        );
    }
}
