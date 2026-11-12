package a4.papers.chatfilter.chatfilter.shared;

public class UnicodeWrapper {

    private final String start;
    private final String end;
    private final int startInt;
    private final int endInt;

    public UnicodeWrapper(String start, String end) {
        this.start = start;
        this.end = end;
        this.startInt = Integer.parseInt(start, 16);
        this.endInt = Integer.parseInt(end, 16);
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }
    
    public int getStartInt() {
        return this.startInt;
    }
    
    public int getEndInt() {
        return this.endInt;
    }

}
