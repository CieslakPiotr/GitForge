package GitForge.repo.internal;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface RepoJdbcRepository extends CrudRepository<RepoEntity, Long> {
    Optional<RepoEntity> findByName(String name);
}
