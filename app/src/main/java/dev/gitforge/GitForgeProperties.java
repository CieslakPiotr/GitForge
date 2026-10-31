package dev.gitforge;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties("gitforge")
public record GitForgeProperties(Storage storage) {

    public record Storage(String root, DataSize maxPushSize) {}
}
