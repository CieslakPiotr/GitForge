package dev.gitforge;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestContainerConfig.class)
@SpringBootTest
abstract class IntegrationTest {}
