package a4.papers.chatfilter.chatfilter.shared;

public class StringSimilarity {

    public static double similarity(String s1, String s2) {
        if (s1.equals(s2)) {
            return 1.0;
        }

        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }

        int lengthDiff = longerLength - shorter.length();
        if (lengthDiff > longerLength / 2) {
            return (double) (longerLength - lengthDiff) / longerLength;
        }

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    public static int editDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        if (len1 == 0) return len2;
        if (len2 == 0) return len1;

        int[] costs = new int[len2 + 1];
        for (int j = 0; j <= len2; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= len1; i++) {
            int lastValue = i;
            char c1 = s1.charAt(i - 1);
            for (int j = 1; j <= len2; j++) {
                int newValue = costs[j - 1];
                if (c1 != s2.charAt(j - 1)) {
                    newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                }
                costs[j - 1] = lastValue;
                lastValue = newValue;
            }
            costs[len2] = lastValue;
        }
        return costs[len2];
    }

}
