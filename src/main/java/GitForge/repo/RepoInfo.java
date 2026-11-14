package GitForge.repo;

import java.time.Instant;

public record RepoInfo(Long id, String name, String head, Instant createdAt) {}