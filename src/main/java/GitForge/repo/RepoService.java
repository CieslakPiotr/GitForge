package GitForge.repo;

import java.time.Instant;

import org.springframework.stereotype.Service;

import GitForge.repo.internal.RepoEntity;
import GitForge.repo.internal.RepoJdbcRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RepoService {

    private final RepoJdbcRepository repos;

    public RepoInfo init(String name) {
        var saved = repos.save(new RepoEntity(null, name, "refs/heads/main", Instant.now()));
        return new RepoInfo(saved.id(), saved.name(), saved.head(), saved.createdAt());
    }
}
