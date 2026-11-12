package a4.papers.chatfilter.chatfilter.shared;

import java.util.regex.Pattern;
import it.unimi.dsi.fastutil.longs.LongArrayList;

public final class CompiledFilter {
    private static final String SEPARATOR = ")+(\\W|_)*";
    private static final long[] NO_REQUIRED_CHARACTERS = new long[0];

    private final Pattern pattern;
    private final FilterWrapper wrapper;
    private final long[] requiredCharacters;

    public CompiledFilter(Pattern pattern, FilterWrapper wrapper) {
        this.pattern = pattern;
        this.wrapper = wrapper;
        this.requiredCharacters = pattern.flags() == 0 ? requiredCharacters(pattern.pattern()) : NO_REQUIRED_CHARACTERS;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public FilterWrapper getWrapper() {
        return this.wrapper;
    }

    public boolean mightMatch(long lowCharacters, long highCharacters) {
        for (int i = 0; i < requiredCharacters.length; i += 2) {
            if ((requiredCharacters[i] & lowCharacters) == 0
                    && (requiredCharacters[i + 1] & highCharacters) == 0) {
                return false;
            }
        }
        return true;
    }

    private static long[] requiredCharacters(String regex) {
        String body = regex;
        if (body.startsWith("\\b")) body = body.substring(2);
        if (body.endsWith("\\b")) body = body.substring(0, body.length() - 2);
        if (!body.startsWith("(") || !body.endsWith(")")) {
            return NO_REQUIRED_CHARACTERS;
        }
        LongArrayList masks = new LongArrayList();
        int position = 1;
        int end = body.length() - 1;
        while (position < end) {
            if (body.charAt(position++) != '(') return NO_REQUIRED_CHARACTERS;
            long low = 0;
            long high = 0;
            while (position < end) {
                char c = body.charAt(position++);
                if (c == '\\' && position < end) {
                    c = body.charAt(position++);
                    if (c != '(' && c != '$') return NO_REQUIRED_CHARACTERS;
                } else if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z')
                        && !(c >= '0' && c <= '9') && c != '@' && c != '!') {
                    return NO_REQUIRED_CHARACTERS;
                }
                if (c < 64) low |= 1L << c;
                else high |= 1L << (c - 64);
                if (position < end && body.charAt(position) == '|') {
                    position++;
                } else {
                    break;
                }
            }
            if (!body.startsWith(SEPARATOR, position)) return NO_REQUIRED_CHARACTERS;
            position += SEPARATOR.length();
            masks.add(low);
            masks.add(high);
        }
        if (position != end) return NO_REQUIRED_CHARACTERS;
        return masks.toLongArray();
    }
}
