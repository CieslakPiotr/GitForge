package dev.gitforge.common.error;

public enum ErrorCode {
    VALIDATION_FAILED(400, "Validation failed"),
    UNAUTHENTICATED(401, "Authentication required"),
    FORBIDDEN(403, "Access denied"),
    NOT_FOUND(404, "Resource not found"),
    CONFLICT(409, "Conflict"),
    INTERNAL_ERROR(500, "Internal server error");

    private final int status;
    private final String title;

    ErrorCode(int status, String title) {
        this.status = status;
        this.title = title;
    }

    public int status() {
        return status;
    }

    public String title() {
        return title;
    }
}
