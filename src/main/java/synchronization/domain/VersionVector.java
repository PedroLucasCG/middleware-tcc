package synchronization.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class VersionVector {
    private Map<String, Long> versions = new HashMap<>();

    public VersionVector() {
    }

    public VersionVector(Map<String, Long> versions) {
        setVersions(versions);
    }

    public VersionVector incremented(String peerId) {
        if (peerId == null || peerId.isBlank()) {
            throw new IllegalArgumentException("peerId cannot be null or blank");
        }

        Map<String, Long> updated = new HashMap<>(versions);
        updated.merge(peerId, 1L, Long::sum);

        return new VersionVector(updated);
    }

    public VersionVector merged(VersionVector other) {
        Objects.requireNonNull(other, "other cannot be null");

        Map<String, Long> merged = new HashMap<>(versions);

        other.versions.forEach(
                (peerId, counter) ->
                        merged.merge(peerId, counter, Math::max)
        );

        return new VersionVector(merged);
    }

    public VectorRelation compare(VersionVector other) {
        Objects.requireNonNull(other, "other cannot be null");

        Set<String> peers = new HashSet<>(versions.keySet());
        peers.addAll(other.versions.keySet());

        boolean localIsSmaller = false;
        boolean localIsGreater = false;

        for (String peerId : peers) {
            long localCounter = versions.getOrDefault(peerId, 0L);
            long remoteCounter = other.versions.getOrDefault(peerId, 0L);

            if (localCounter < remoteCounter) {
                localIsSmaller = true;
            }

            if (localCounter > remoteCounter) {
                localIsGreater = true;
            }

            if (localIsSmaller && localIsGreater) {
                return VectorRelation.CONCURRENT;
            }
        }

        if (localIsSmaller) {
            return VectorRelation.BEFORE;
        }

        if (localIsGreater) {
            return VectorRelation.AFTER;
        }

        return VectorRelation.EQUAL;
    }

    public long getCounter(String peerId) {
        return versions.getOrDefault(peerId, 0L);
    }

    public Map<String, Long> getVersions() {
        return Map.copyOf(versions);
    }

    public void setVersions(Map<String, Long> versions) {
        this.versions = versions == null
                ? new HashMap<>()
                : new HashMap<>(versions);
    }

    @Override
    public String toString() {
        return versions.toString();
    }
}
