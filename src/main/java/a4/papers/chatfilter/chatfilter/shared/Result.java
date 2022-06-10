package a4.papers.chatfilter.chatfilter.shared;

import java.util.List;

public class Result {
    private final boolean result;
    private final String[] message;
    private final Types type;
    private FilterWrapper filterWrapper;
    private final List<String> regexList;


    public Result(boolean result, String[] array, Types type, FilterWrapper filterWrapper, List<String> regexList) {
        this.result = result;
        this.message = array;
        this.type = type;
        this.filterWrapper = filterWrapper;
        this.regexList = regexList;

    }

    public String[] getStringArray() {
        return message;
    }

    public List<String> getRegexList() {
        return regexList;
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
