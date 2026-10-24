package a4.papers.chatfilter.chatfilter.shared;

public record FilterSnapshot(CompiledFilter[] words, CompiledFilter[] adverts) {
    public static final FilterSnapshot EMPTY = new FilterSnapshot(new CompiledFilter[0], new CompiledFilter[0]);
}
