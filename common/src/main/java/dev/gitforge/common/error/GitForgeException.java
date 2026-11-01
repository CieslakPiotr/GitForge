package dev.gitforge.common.error;

public class GitForgeException extends RuntimeException {

    private final ErrorCode code;

    public GitForgeException(ErrorCode code, String detail) {
        super(detail);
        this.code = code;
    }

    public ErrorCode code() {
        return code;
    }
}
