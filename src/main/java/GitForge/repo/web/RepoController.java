package GitForge.repo.web;

import java.net.URI;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import GitForge.repo.RepoInfo;
import GitForge.repo.RepoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/repos")
@RequiredArgsConstructor
class RepoController {

    private final RepoService service;

    @PostMapping
    ResponseEntity<RepoInfo> init(@Valid @RequestBody InitRepoRequest request) {
        var repo = service.init(request.name());
        return ResponseEntity.created(URI.create("/api/repos/" + repo.name())).body(repo);
    }

    // Spring Data JDBC wraps database errors; a duplicate name arrives as DuplicateKeyException inside it.
    @ExceptionHandler(DbActionExecutionException.class)
    ResponseEntity<String> saveFailed(DbActionExecutionException e) {
        if (e.getCause() instanceof DuplicateKeyException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Repository already exists");
        }
        throw e;
    }
}
