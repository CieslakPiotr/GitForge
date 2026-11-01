package dev.gitforge;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.unit.DataSize;

class GitForgePropertiesTest extends IntegrationTest {

    @Autowired GitForgeProperties properties;

    @Test
    void loadsStorageSettings() {
        assertThat(properties.storage().root()).isEqualTo("./data/repos");
        assertThat(properties.storage().maxPushSize()).isEqualTo(DataSize.ofGigabytes(2));
    }
}
