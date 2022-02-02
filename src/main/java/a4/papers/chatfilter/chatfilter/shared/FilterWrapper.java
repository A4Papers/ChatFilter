package a4.papers.chatfilter.chatfilter.shared;

public class FilterWrapper {

    private final String command;
    private final String word;
    private final String regex;
    private final String replace;

    private final boolean sendStaff;
    private final boolean logToConsole;
    private final boolean cancelChat;
    private final boolean warnPlayer;

    public FilterWrapper(String word, String command, String regex,boolean cancelChat, String replace, boolean sendStaff, boolean logToConsole,boolean warnPlayer) {
        this.command = command;
        this.regex = regex;
        this.replace = replace;
        this.word = word;
        this.sendStaff = sendStaff;
        this.logToConsole = logToConsole;
        this.cancelChat = cancelChat;
        this.warnPlayer = warnPlayer;
    }
    public boolean getLogToConsole() {
        return this.logToConsole;
    }
    public boolean getSendStaff() {
        return this.sendStaff;
    }
    public boolean getCancelChat() {
        return this.cancelChat;
    }
    public boolean getWarnPlayer() {
        return this.warnPlayer;
    }

    public String getCommand() {
        return this.command;
    }
    public String getRegex() {
        return this.regex;
    }
    public String getReplace() {
        return this.replace;
    }
    public String getWord() {
        return this.word;
    }
}
