package a4.papers.chatfilter.chatfilter.shared;

import java.util.Collection;
import java.util.List;

public final class LiteralReplacements {
    public static final LiteralReplacements EMPTY = new LiteralReplacements(List.of());

    private final List<String> literals;
    private final LiteralMatcher matcher;

    public LiteralReplacements(Collection<String> literals) {
        this.literals = List.copyOf(literals);
        matcher = new LiteralMatcher(this.literals);
    }

    public String replace(String text, String replacement) {
        if (!literals.isEmpty() && matcher.containsAny(text)) {
            for (String literal : literals) {
                text = text.replace(literal, replacement);
            }
        }
        return text;
    }
}
