package GitForge.repo.internal;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("repositories")
public record RepoEntity(@Id Long id, String name, String head, Instant createdAt) {}
