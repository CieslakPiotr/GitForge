package dev.gitforge.common.id;

import java.util.regex.Pattern;

public record CommitSha(String hex) {

    private static final Pattern FORMAT = Pattern.compile("[0-9a-f]{40}");

    public CommitSha {
        if (hex == null || !FORMAT.matcher(hex).matches()) {
            throw new IllegalArgumentException("Invalid commit SHA: " + hex);
        }
    }
}
