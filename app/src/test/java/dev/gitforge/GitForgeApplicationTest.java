package dev.gitforge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestDatabaseConfig.class)
@SpringBootTest
class GitForgeApplicationTest {

    @Test
    void contextLoads() {}
}
