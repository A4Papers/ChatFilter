package a4.papers.chatfilter.chatfilter.shared;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class CodePointIndexTest {
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
