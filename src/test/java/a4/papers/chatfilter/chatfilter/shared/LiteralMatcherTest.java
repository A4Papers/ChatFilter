package a4.papers.chatfilter.chatfilter.shared;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

class LiteralMatcherTest {
    @Test
    void largeAlphabetAndManyBranchingStatesPreserveFallbackMatches() {
        List<String> literals = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            String binary = Integer.toBinaryString(i | 256).substring(1);
            literals.add(binary + (char) (i & 127));
        }
        LiteralMatcher matcher = new LiteralMatcher(literals);
        for (String literal : literals) assertTrue(matcher.containsAny("011010110" + literal));
        Random random = new Random(511);
        for (int i = 0; i < 2000; i++) {
            StringBuilder message = new StringBuilder();
            for (int j = 0; j < 40; j++) message.append((char) (random.nextBoolean() ? '0' : '1'));
            message.append((char) random.nextInt(128));
            String text = message.toString();
            assertEquals(literals.stream().anyMatch(text::contains), matcher.containsAny(text));
        }
    }

    @Test
    void supportsAllAsciiCharactersInTheTransitionAlphabet() {
        List<String> literals = new ArrayList<>();
        for (char c = 0; c < 128; c++) literals.add(c + "end");
        LiteralMatcher matcher = new LiteralMatcher(literals);
        for (String literal : literals) assertTrue(matcher.containsAny("prefix" + literal));
        assertFalse(matcher.containsAny("\uFF21end"));
    }

    @Test
    void handlesLargeBranchingUnicodeAndSuffixFallbacks() {
        List<String> literals = new ArrayList<>(List.of("he", "she", "his", "hers", "aab", "bcdef"));
        for (int c = 0; c <= Character.MAX_VALUE; c += 257) literals.add((char) c + "xyz");
        LiteralMatcher matcher = new LiteralMatcher(literals);
        Random random = new Random(94);
        for (int trial = 0; trial < 3000; trial++) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < 70; i++) text.append((char) random.nextInt(65536));
            if (trial % 2 == 0) text.insert(random.nextInt(71), literals.get(random.nextInt(literals.size())));
            String message = text.toString();
            assertEquals(literals.stream().anyMatch(message::contains), matcher.containsAny(message));
        }
        assertTrue(matcher.containsAny("aaaaaab"));
        assertTrue(matcher.containsAny("ushers"));
        assertTrue(matcher.containsAny("abcd bcdef"));
        assertFalse(new LiteralMatcher(List.of()).containsAny("anything"));
        assertTrue(new LiteralMatcher(List.of("")).containsAny(""));
    }

    @Test
    void supportsConcurrentReadersWithoutPerThreadState() throws Exception {
        LiteralMatcher matcher = new LiteralMatcher(List.of("he", "she", "hers", "his", "\uD83D\uDE00"));
        try (var executor = Executors.newFixedThreadPool(8)) {
            List<Callable<Boolean>> calls = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                calls.add(() -> matcher.containsAny("ushers") && matcher.containsAny("\uD83D\uDE00")
                        && !matcher.containsAny("clean message"));
            }
            for (var result : executor.invokeAll(calls)) assertTrue(result.get());
        }
    }
}
