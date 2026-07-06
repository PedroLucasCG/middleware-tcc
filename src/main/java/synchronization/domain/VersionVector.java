package synchronization.domain;

import transport.domain.NodeConfig;

import java.util.*;

public final class VersionVector {
    private Map<UUID, Long> versions = new HashMap<>();

    public VersionVector() {
    }

    public VersionVector(Map<UUID, Long> versions) {
        versions(versions);
    }

    public VersionVector incremented(UUID peerId) {
        if (peerId == null) {
            throw new IllegalArgumentException("peerId cannot be null or blank");
        }

        Map<UUID, Long> updated = new HashMap<>(versions);
        updated.merge(peerId, 1L, Long::sum);

        return new VersionVector(updated);
    }

    public VersionVector merged(VersionVector other) {
        Objects.requireNonNull(other, "other cannot be null");

        Map<UUID, Long> merged = new HashMap<>(versions);

        other.versions.forEach(
                (peerId, counter) ->
                        merged.merge(peerId, counter, Math::max)
        );

        return new VersionVector(merged);
    }

    public VectorRelation compare(VersionVector other) {
        Objects.requireNonNull(other, "other cannot be null");

        Set<UUID> peers = new HashSet<>(versions.keySet());
        peers.addAll(other.versions.keySet());

        boolean localIsSmaller = false;
        boolean localIsGreater = false;

        for (UUID peerId : peers) {
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

    public Map<UUID, Long> getVersions() {
        return Map.copyOf(versions);
    }

    private void versions(Map<UUID, Long> versions) {
        this.versions = versions == null
                ? new HashMap<>()
                : new HashMap<>(versions);
    }

    public String toString() {
        return versions.toString();
    }
}
