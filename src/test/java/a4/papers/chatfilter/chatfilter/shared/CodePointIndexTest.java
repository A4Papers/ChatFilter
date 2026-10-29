package a4.papers.chatfilter.chatfilter.shared;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class CodePointIndexTest {
    @Test
    void mergesOverlappingAndAdjacentRangesAndClampsToUnicode() {
        CodePointIndex index = new CodePointIndex(List.of(new UnicodeWrapper("40", "4F"),
                new UnicodeWrapper("10", "20"), new UnicodeWrapper("18", "30"),
                new UnicodeWrapper("31", "3F"), new UnicodeWrapper("10FFFF", "7FFFFFFF"),
                new UnicodeWrapper("50", "40")));
        for (int cp = 0; cp <= Character.MAX_CODE_POINT + 1; cp++) {
            assertEquals(cp >= 0x10 && cp <= 0x4F || cp == Character.MAX_CODE_POINT, index.contains(cp));
        }
        assertFalse(index.contains(Integer.MAX_VALUE));
        assertFalse(index.contains(Integer.MIN_VALUE));
        assertTrue(new CodePointIndex(List.of(new UnicodeWrapper("2", "1"))).isEmpty());
    }

    @Test
    void compactBitmapHandlesWordBoundariesAndHoles() {
        List<UnicodeWrapper> ranges = new ArrayList<>();
        for (int cp = 0x10000; cp < 0x10200; cp += 2) {
            ranges.add(new UnicodeWrapper(Integer.toHexString(cp), Integer.toHexString(cp)));
        }
        CodePointIndex index = new CodePointIndex(ranges);
        for (int cp = 0; cp <= Character.MAX_CODE_POINT; cp++) {
            assertEquals(cp >= 0x10000 && cp < 0x10200 && (cp & 1) == 0, index.contains(cp));
        }
    }

    @Test
    void matchesRangeScanAcrossEveryUnicodeCodePoint() {
        List<UnicodeWrapper> ranges = new ArrayList<>(List.of(new UnicodeWrapper("FF21", "FF3A"),
                new UnicodeWrapper("FF41", "FF5A"), new UnicodeWrapper("24D0", "24CF"),
                new UnicodeWrapper("1F600", "1F64F"), new UnicodeWrapper("10FFFE", "10FFFF")));
        Random random = new Random(71);
        for (int i = 0; i < 20; i++) {
            int start = random.nextInt(Character.MAX_CODE_POINT);
            ranges.add(new UnicodeWrapper(Integer.toHexString(start), Integer.toHexString(start + random.nextInt(100))));
        }
        CodePointIndex index = new CodePointIndex(ranges);
        for (int cp = 0; cp <= Character.MAX_CODE_POINT; cp++) {
            boolean expected = false;
            for (UnicodeWrapper range : ranges) expected |= cp >= range.getStartInt() && cp <= range.getEndInt();
            assertEquals(expected, index.contains(cp));
        }
        assertFalse(index.isEmpty());
        assertTrue(CodePointIndex.EMPTY.isEmpty());
        assertFalse(index.contains(-1));
    }
}
