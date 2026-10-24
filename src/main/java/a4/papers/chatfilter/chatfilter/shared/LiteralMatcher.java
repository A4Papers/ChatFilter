package a4.papers.chatfilter.chatfilter.shared;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class LiteralMatcher {
    private final Node root = new Node();

    public LiteralMatcher(Collection<String> literals) {
        root.failure = root;
        for (String literal : literals) {
            Node node = root;
            for (int i = 0; i < literal.length(); i++) {
                node = node.children.computeIfAbsent(literal.charAt(i), key -> new Node());
            }
            node.terminal = true;
        }
        ArrayDeque<Node> queue = new ArrayDeque<>();
        for (Node child : root.children.values()) {
            child.failure = root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            Node node = queue.remove();
            for (Map.Entry<Character, Node> entry : node.children.entrySet()) {
                char c = entry.getKey();
                Node child = entry.getValue();
                Node fallback = node.failure;
                while (fallback != root && !fallback.children.containsKey(c)) {
                    fallback = fallback.failure;
                }
                Node suffix = fallback.children.get(c);
                child.failure = suffix == null ? root : suffix;
                child.terminal |= child.failure.terminal;
                queue.add(child);
            }
        }
    }

    public boolean containsAny(String text) {
        if (root.terminal) {
            return true;
        }
        Node node = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Node next = node.children.get(c);
            while (next == null && node != root) {
                node = node.failure;
                next = node.children.get(c);
            }
            node = next == null ? root : next;
            if (node.terminal) {
                return true;
            }
        }
        return false;
    }

    private static final class Node {
        private final Map<Character, Node> children = new HashMap<>();
        private Node failure;
        private boolean terminal;
    }
}
