package dev.gitforge;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

    @Test
    void modulesFollowTheirBoundaries() {
        ApplicationModules.of(GitForgeApplication.class).verify();
    }
}
