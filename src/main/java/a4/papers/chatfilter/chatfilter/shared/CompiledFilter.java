package a4.papers.chatfilter.chatfilter.shared;

import java.util.regex.Pattern;

public final class CompiledFilter {

    private final Pattern pattern;
    private final FilterWrapper wrapper;

    public CompiledFilter(Pattern pattern, FilterWrapper wrapper) {
        this.pattern = pattern;
        this.wrapper = wrapper;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public FilterWrapper getWrapper() {
        return this.wrapper;
    }
}
