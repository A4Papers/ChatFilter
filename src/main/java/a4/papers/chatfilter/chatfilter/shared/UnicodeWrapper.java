package a4.papers.chatfilter.chatfilter.shared;

public class UnicodeWrapper {

    private final String start;
    private final String end;

    public UnicodeWrapper(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }

}
