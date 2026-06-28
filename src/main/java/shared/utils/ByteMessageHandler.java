package shared.utils;

import synchronization.domain.Annotation;
import synchronization.domain.TransactionRecord;

import java.time.Instant;

public class ByteMessageHandler {
    public static String serialize(TransactionRecord record) {
        return String.join("|",
                record.getId(),
                record.getMessage(),
                record.getUpdatedAt().toString(),
                record.getNodeId(),
                String.valueOf(record.isDeleted())
        );
    }

    public static TransactionRecord deserialize(String raw) {
        String[] parts = raw.split("\\|", -1);

        return new TransactionRecord(
                new Annotation(parts[1]),
                parts[3]
        );
    }
}
