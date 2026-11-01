package dev.gitforge.common.id;

import java.util.Objects;
import java.util.UUID;

public record RepoId(UUID value) {
    public RepoId {
        Objects.requireNonNull(value, "value must not be null");
    }
}
