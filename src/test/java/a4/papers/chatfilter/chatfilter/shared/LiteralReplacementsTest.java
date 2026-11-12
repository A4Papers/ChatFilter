package a4.papers.chatfilter.chatfilter.shared;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class LiteralReplacementsTest {
    @Test
    void preservesOrderedOverlappingAndCascadingReplacements() {
        for (List<String> words : List.of(List.of("ab", " c"), List.of("ab", "bc", "a"),
                List.of("ab", "ab", " "), List.of("", "a"), List.of("he", "she", "his", "hers"))) {
            verify(words, "zabc she and hers ab", " ");
            verify(words, "zabc she and hers ab", "");
        }
    }

    @Test
    void matchesSequentialReferenceForRandomizedInputs() {
        Random random = new Random(31);
        for (int trial = 0; trial < 3000; trial++) {
            List<String> words = new ArrayList<>();
            for (int count = random.nextInt(20); count > 0; count--) {
                words.add(text(random, random.nextInt(7)));
            }
            String text = text(random, random.nextInt(100));
            verify(words, text, " ");
            verify(words, text, "");
            verify(words, text, "$\\");
        }
    }

    @Test
    void keepsUnchangedMessagesAndDefendsAgainstListMutation() {
        List<String> words = new ArrayList<>(List.of("missing"));
        LiteralReplacements replacements = new LiteralReplacements(words);
        words.add("text");
        String message = new String("text");
        assertSame(message, replacements.replace(message, " "));
        assertSame(message, LiteralReplacements.EMPTY.replace(message, " "));
    }

    private static void verify(List<String> words, String text, String replacement) {
        String expected = text;
        for (String word : words) expected = expected.replace(word, replacement);
        assertEquals(expected, new LiteralReplacements(words).replace(text, replacement));
        boolean contains = false;
        for (String word : words) contains |= text.contains(word);
        assertEquals(contains, new LiteralMatcher(words).containsAny(text));
    }

    private static String text(Random random, int length) {
        String alphabet = "abc \u03b1\uD83D\uDE00";
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) builder.append(alphabet.charAt(random.nextInt(alphabet.length())));
        return builder.toString();
    }
}
