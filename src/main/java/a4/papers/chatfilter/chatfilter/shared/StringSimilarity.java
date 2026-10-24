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
        return distance(s1, s2, Math.max(s1.length(), s2.length()));
    }

    public static boolean exceedsSimilarity(String s1, String s2, double threshold) {
        if (Double.isNaN(threshold) || threshold >= 1.0) {
            return false;
        }
        if (threshold < 0.0 || s1.equals(s2)) {
            return true;
        }
        int length = Math.max(s1.length(), s2.length());
        int difference = Math.abs(s1.length() - s2.length());
        if (difference > length / 2) {
            return (double) (length - difference) / length > threshold;
        }
        int limit = (int) Math.ceil(length * (1.0 - threshold)) - 1;
        while (limit >= 0 && (length - limit) / (double) length <= threshold) {
            limit--;
        }
        while (limit < length && (length - limit - 1) / (double) length > threshold) {
            limit++;
        }
        return difference <= limit && distance(s1, s2, limit) <= limit;
    }

    private static int distance(String s1, String s2, int limit) {
        if (s1.length() < s2.length()) {
            String swap = s1;
            s1 = s2;
            s2 = swap;
        }
        int start = 0;
        int end1 = s1.length();
        int end2 = s2.length();
        while (start < end2 && s1.charAt(start) == s2.charAt(start)) {
            start++;
        }
        while (end2 > start && s1.charAt(end1 - 1) == s2.charAt(end2 - 1)) {
            end1--;
            end2--;
        }
        int rows = end1 - start;
        int columns = end2 - start;
        if (columns == 0) {
            return rows;
        }
        if (rows - columns > limit) {
            return limit + 1;
        }
        int[] costs = new int[columns + 1];
        for (int j = 0; j <= columns; j++) {
            costs[j] = j <= limit ? j : limit + 1;
        }
        for (int i = 1; i <= rows; i++) {
            int first = Math.max(1, i - limit);
            int last = Math.min(columns, i + limit);
            int diagonal = costs[first - 1];
            costs[first - 1] = first == 1 ? i : limit + 1;
            int minimum = limit + 1;
            char c = s1.charAt(start + i - 1);
            for (int j = first; j <= last; j++) {
                int previous = costs[j];
                int value = c == s2.charAt(start + j - 1) ? diagonal
                        : Math.min(diagonal, Math.min(costs[j - 1], previous)) + 1;
                costs[j] = value;
                diagonal = previous;
                minimum = Math.min(minimum, value);
            }
            if (last < columns) {
                costs[last + 1] = limit + 1;
            }
            if (minimum > limit) {
                return limit + 1;
            }
        }
        return costs[columns];
    }

}
