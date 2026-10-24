package a4.papers.chatfilter.chatfilter.shared;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

public final class CodePointIndex {
    public static final CodePointIndex EMPTY = new CodePointIndex(List.of());

    private final BitSet blocked = new BitSet();

    public CodePointIndex(Collection<UnicodeWrapper> ranges) {
        for (UnicodeWrapper range : ranges) {
            int start = Math.max(Character.MIN_CODE_POINT, range.getStartInt());
            int end = Math.min(Character.MAX_CODE_POINT, range.getEndInt());
            if (start <= end) {
                blocked.set(start, end + 1);
            }
        }
    }

    public boolean isEmpty() {
        return blocked.isEmpty();
    }

    public boolean contains(int codePoint) {
        return codePoint >= 0 && blocked.get(codePoint);
    }
}
