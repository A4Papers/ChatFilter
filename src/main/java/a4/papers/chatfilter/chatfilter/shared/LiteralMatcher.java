package a4.papers.chatfilter.chatfilter.shared;

import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public final class LiteralMatcher {
    private final int[] offsets;
    private final char[] labels;
    private final int[] targets;
    private final int[] failures;
    private final boolean[] terminal;
    private final byte[] asciiAlphabet;
    private final int[] asciiRows;
    private final int[] asciiTransitions;

    public LiteralMatcher(Collection<String> literals) {
        ArrayList<Builder> nodes = new ArrayList<>();
        nodes.add(new Builder());
        for (String literal : literals) {
            int state = 0;
            for (int i = 0; i < literal.length(); i++) {
                Builder node = nodes.get(state);
                if (node.children == null) {
                    node.children = new Char2IntOpenHashMap(2);
                    node.children.defaultReturnValue(-1);
                }
                char c = literal.charAt(i);
                int next = node.children.get(c);
                if (next < 0) {
                    next = nodes.size();
                    node.children.put(c, next);
                    nodes.add(new Builder());
                }
                state = next;
            }
            nodes.get(state).terminal = true;
        }

        int size = nodes.size();
        offsets = new int[size + 1];
        labels = new char[size - 1];
        targets = new int[size - 1];
        failures = new int[size];
        terminal = new boolean[size];
        int edge = 0;
        for (int state = 0; state < size; state++) {
            Builder node = nodes.get(state);
            offsets[state] = edge;
            terminal[state] = node.terminal;
            if (node.children != null) {
                char[] keys = node.children.keySet().toCharArray();
                Arrays.sort(keys);
                for (char c : keys) {
                    labels[edge] = c;
                    targets[edge++] = node.children.get(c);
                }
            }
        }
        offsets[size] = edge;
        int[] queue = new int[size];
        int head = 0;
        int tail = 0;
        for (int i = 0; i < offsets[1]; i++) queue[tail++] = targets[i];
        while (head < tail) {
            int state = queue[head++];
            for (int i = offsets[state]; i < offsets[state + 1]; i++) {
                char c = labels[i];
                int child = targets[i];
                int fallback = failures[state];
                int suffix = sparseTransition(fallback, c);
                while (suffix < 0 && fallback != 0) {
                    fallback = failures[fallback];
                    suffix = sparseTransition(fallback, c);
                }
                failures[child] = suffix < 0 ? 0 : suffix;
                terminal[child] |= terminal[failures[child]];
                queue[tail++] = child;
            }
        }

        asciiAlphabet = new byte[128];
        for (char c : labels) if (c < 128) asciiAlphabet[c] = 1;
        int alphabetSize = 0;
        for (int c = 0; c < 128; c++) if (asciiAlphabet[c] != 0) asciiAlphabet[c] = (byte) ++alphabetSize;
        asciiRows = new int[size];
        int slots = 1;
        long budget = Math.max(alphabetSize + 1L, size * 4L);
        for (int position = -1; position < tail; position++) {
            int state = position < 0 ? 0 : queue[position];
            if (slots + (long) alphabetSize <= budget
                    && (state == 0 || (offsets[state + 1] - offsets[state] > 1 && labels[offsets[state]] < 128))) {
                asciiRows[state] = slots;
                slots = Math.addExact(slots, alphabetSize);
            }
        }
        asciiTransitions = new int[slots];
        for (int state = 0; state < size; state++) {
            int row = asciiRows[state];
            if (row == 0) continue;
            for (char c = 0; c < 128; c++) {
                int column = asciiAlphabet[c] & 0xFF;
                if (column == 0) continue;
                int fallback = state;
                int next = sparseTransition(fallback, c);
                while (next < 0 && fallback != 0) {
                    fallback = failures[fallback];
                    next = sparseTransition(fallback, c);
                }
                asciiTransitions[row + column - 1] = next < 0 ? 0 : next;
            }
        }
    }

    public boolean containsAny(String text) {
        if (terminal[0]) return true;
        if (targets.length == 0) return false;
        int state = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int next = transition(state, c);
            while (next < 0 && state != 0) {
                state = failures[state];
                next = transition(state, c);
            }
            state = next < 0 ? 0 : next;
            if (terminal[state]) return true;
        }
        return false;
    }

    private int transition(int state, char c) {
        if (c < 128) {
            int column = asciiAlphabet[c] & 0xFF;
            if (column == 0) return 0;
            int row = asciiRows[state];
            if (row != 0) return asciiTransitions[row + column - 1];
        }
        return sparseTransition(state, c);
    }

    private int sparseTransition(int state, char c) {
        int first = offsets[state];
        int end = offsets[state + 1];
        if (end - first <= 4) {
            for (int i = first; i < end; i++) {
                if (labels[i] == c) return targets[i];
            }
            return -1;
        }
        int index = Arrays.binarySearch(labels, first, end, c);
        return index < 0 ? -1 : targets[index];
    }

    private static final class Builder {
        private Char2IntOpenHashMap children;
        private boolean terminal;
    }
}
