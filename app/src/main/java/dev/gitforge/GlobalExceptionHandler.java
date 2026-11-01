package dev.gitforge;

import dev.gitforge.common.error.ErrorCode;
import dev.gitforge.common.error.GitForgeException;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GitForgeException.class)
    ResponseEntity<ProblemDetail> handleGitForgeException(GitForgeException ex) {
        ProblemDetail problem = problem(ex.code(), ex.getMessage());
        return ResponseEntity.status(ex.code().status()).body(problem);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorCode code = ErrorCode.INTERNAL_ERROR;
        ProblemDetail problem = problem(code, "An unexpected error occurred.");
        return ResponseEntity.status(code.status()).body(problem);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ErrorCode code = ErrorCode.VALIDATION_FAILED;
        ProblemDetail problem = problem(code, "One or more fields are invalid.");
        List<Map<String, String>> errors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(
                                error ->
                                        Map.of(
                                                "field",
                                                error.getField(),
                                                "message",
                                                String.valueOf(error.getDefaultMessage())))
                        .toList();
        problem.setProperty("errors", errors);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private static ProblemDetail problem(ErrorCode code, String detail) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(code.status()), detail);
        problem.setType(URI.create("urn:gitforge:error:" + code.name().toLowerCase(Locale.ROOT)));
        problem.setTitle(code.title());
        problem.setProperty("code", code.name());
        return problem;
    }
}
