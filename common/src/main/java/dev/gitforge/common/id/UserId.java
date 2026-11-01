package dev.gitforge.common.id;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        Objects.requireNonNull(value, "value must not be null");
    }
}
