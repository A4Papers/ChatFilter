package a4.papers.chatfilter.chatfilter.shared;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class StringSimilarityTest {
    @Test
    void matchesReferenceDistanceAndThresholdDecisions() {
        Random random = new Random(542);
        for (int sample = 0; sample < 5000; sample++) {
            String first = randomString(random);
            String second = sample % 3 == 0 ? first + randomString(random) : randomString(random);
            int expected = referenceDistance(first, second);
            assertEquals(expected, StringSimilarity.editDistance(first, second));
            assertEquals(expected, StringSimilarity.editDistance(second, first));
            int length = Math.max(first.length(), second.length());
            int difference = Math.abs(first.length() - second.length());
            double similarity = length == 0 ? 1.0 : difference > length / 2
                    ? (length - difference) / (double) length : (length - expected) / (double) length;
            assertEquals(similarity, StringSimilarity.similarity(first, second));
            for (double threshold : new double[]{-1, 0, 0.1, 0.6, 0.99, 1, Double.NaN,
                    Math.nextDown(similarity), similarity, Math.nextUp(similarity)}) {
                assertEquals(similarity > threshold, StringSimilarity.exceedsSimilarity(first, second, threshold),
                        () -> first + " / " + second + " at " + threshold);
            }
        }
    }

    @Test
    void trimsLongSharedAffixes() {
        String prefix = "a".repeat(20000);
        String suffix = "b".repeat(20000);
        assertEquals(1, StringSimilarity.editDistance(prefix + "x" + suffix, prefix + "y" + suffix));
        assertTrue(StringSimilarity.exceedsSimilarity(prefix + "x" + suffix, prefix + "y" + suffix, 0.99));
        assertFalse(StringSimilarity.exceedsSimilarity("a".repeat(20000), "b".repeat(20000), 0.99));
    }

    private static String randomString(Random random) {
        String alphabet = "abc def\u0130\uD83D\uDE00";
        StringBuilder text = new StringBuilder();
        for (int length = random.nextInt(45); length > 0; length--) {
            text.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return text.toString();
    }

    static int referenceDistance(String first, String second) {
        int[][] matrix = new int[first.length() + 1][second.length() + 1];
        for (int i = 0; i <= first.length(); i++) matrix[i][0] = i;
        for (int j = 0; j <= second.length(); j++) matrix[0][j] = j;
        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                matrix[i][j] = Math.min(matrix[i - 1][j] + 1, Math.min(matrix[i][j - 1] + 1,
                        matrix[i - 1][j - 1] + (first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1)));
            }
        }
        return matrix[first.length()][second.length()];
    }
}
