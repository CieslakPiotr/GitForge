package dev.gitforge.common.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class CommitShaTest {

    @Test
    void acceptsFortyLowercaseHexCharacters() {
        String hex = "a".repeat(40);

        assertThat(new CommitSha(hex).hex()).isEqualTo(hex);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "abc123", "ABCDEFABCDEFABCDEFABCDEFABCDEFABCDEFABCD"})
    void rejectsInvalidValues(String hex) {
        assertThatThrownBy(() -> new CommitSha(hex)).isInstanceOf(IllegalArgumentException.class);
    }
}
