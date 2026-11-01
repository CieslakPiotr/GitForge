package dev.gitforge.common.paging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PageTest {

    @Test
    void hasNextWhenCursorIsPresent() {
        assertThat(new Page<>(List.of("a"), "next").hasNext()).isTrue();
        assertThat(new Page<>(List.of("a"), null).hasNext()).isFalse();
    }

    @Test
    void itemsCannotBeChangedAfterCreation() {
        List<String> source = new ArrayList<>(List.of("a"));
        Page<String> page = new Page<>(source, null);

        source.add("b");

        assertThat(page.items()).containsExactly("a");
        assertThatThrownBy(() -> page.items().add("c"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
