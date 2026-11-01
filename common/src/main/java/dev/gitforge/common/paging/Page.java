package dev.gitforge.common.paging;

import java.util.List;

public record Page<T>(List<T> items, String nextCursor) {

    public Page {
        items = List.copyOf(items);
    }

    public boolean hasNext() {
        return nextCursor != null;
    }
}
