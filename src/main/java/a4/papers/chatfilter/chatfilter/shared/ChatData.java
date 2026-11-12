package a4.papers.chatfilter.chatfilter.shared;

public class ChatData {

    private final String string;
    private final long lo;

    public ChatData(String string, long lo) {
        this.string = string;
        this.lo = lo;
    }

    public String getString() {
        return this.string;
    }

    public long getLong() {
        return this.lo;
    }

}
