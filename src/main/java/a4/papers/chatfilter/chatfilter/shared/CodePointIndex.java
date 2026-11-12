package a4.papers.chatfilter.chatfilter.shared;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

public final class CodePointIndex {
    private static final int[] NO_RANGES = new int[0];
    private static final long[] NO_BITS = new long[0];
    public static final CodePointIndex EMPTY = new CodePointIndex(List.of());

    private final int minimum;
    private final int maximum;
    private final int[] boundaries;
    private final long[] bits;

    public CodePointIndex(Collection<UnicodeWrapper> ranges) {
        long[] sorted = new long[ranges.size()];
        int count = 0;
        for (UnicodeWrapper range : ranges) {
            int start = Math.max(Character.MIN_CODE_POINT, range.getStartInt());
            int end = Math.min(Character.MAX_CODE_POINT, range.getEndInt());
            if (start <= end) sorted[count++] = ((long) start << 32) | (end + 1L);
        }
        Arrays.sort(sorted, 0, count);
        int[] merged = new int[count * 2];
        int used = 0;
        for (int i = 0; i < count; i++) {
            int start = (int) (sorted[i] >>> 32);
            int end = (int) sorted[i];
            if (used > 0 && start <= merged[used - 1]) {
                merged[used - 1] = Math.max(merged[used - 1], end);
            } else {
                merged[used++] = start;
                merged[used++] = end;
            }
        }
        minimum = used == 0 ? 0 : merged[0];
        maximum = used == 0 ? -1 : merged[used - 1] - 1;
        int words = (maximum - minimum + 64) >>> 6;
        if (used > 0 && words * Long.BYTES < used * Integer.BYTES) {
            BitSet bitmap = new BitSet(maximum - minimum + 1);
            for (int i = 0; i < used; i += 2) bitmap.set(merged[i] - minimum, merged[i + 1] - minimum);
            bits = bitmap.toLongArray();
            boundaries = NO_RANGES;
        } else {
            boundaries = used == 0 ? NO_RANGES : Arrays.copyOf(merged, used);
            bits = NO_BITS;
        }
    }

    public boolean isEmpty() {
        return maximum < minimum;
    }

    public boolean contains(int codePoint) {
        if (bits.length != 0) {
            int relative = codePoint - minimum;
            return relative >= 0 && (relative >>> 6) < bits.length
                    && (bits[relative >>> 6] & (1L << relative)) != 0;
        }
        if (codePoint < minimum || codePoint > maximum) return false;
        int index = Arrays.binarySearch(boundaries, codePoint);
        return index >= 0 ? (index & 1) == 0 : (-index & 1) == 0;
    }
}
