package a4.papers.chatfilter.chatfilter.shared;

public class Result {
    private final boolean result;
    private final String[] message;
    private final Types type;
    private FilterWrapper filterWrapper;

    public Result(boolean result, String[] array, Types type, FilterWrapper filterWrapper) {
        this.result = result;
        this.message = array;
        this.type = type;
        this.filterWrapper = filterWrapper;
    }

    public String[] getStringArray() {
        return message;
    }

    public boolean getResult() {
        return result;
    }

    public Types getType() {
        return type;
    }

    public FilterWrapper getFilterWrapper() {
        return filterWrapper;
    }
}
