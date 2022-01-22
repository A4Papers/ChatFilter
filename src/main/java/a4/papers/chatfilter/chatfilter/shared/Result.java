package a4.papers.chatfilter.chatfilter.shared;

public class Result {
    private final boolean result;
    private final String[] message;
    private final Types type;
    private final String regexUse;

    public Result(boolean result, String[] array, Types type, String regexUse) {
        this.result = result;
        this.message = array;
        this.type = type;
        this.regexUse = regexUse;
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

    public String getRegexPattern() {
        return regexUse;
    }
}
