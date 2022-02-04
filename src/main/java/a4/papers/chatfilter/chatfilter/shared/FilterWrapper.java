package a4.papers.chatfilter.chatfilter.shared;

import java.util.List;

public class FilterWrapper {

    private final List<String> action;
    private final String word;
    private final String regex;
    private final String replace;
    private final boolean cancelChatReplace;
    private final boolean sendStaff;
    private final boolean logToConsole;
    private final boolean cancelChat;
    private final boolean warnPlayer;

    public FilterWrapper(String word, List<String> action, String regex, boolean cancelChat, boolean cancelChatReplace, String replace, boolean sendStaff, boolean logToConsole, boolean warnPlayer) {
        this.action = action;
        this.regex = regex;
        this.replace = replace;
        this.word = word;
        this.sendStaff = sendStaff;
        this.logToConsole = logToConsole;
        this.cancelChat = cancelChat;
        this.cancelChatReplace = cancelChatReplace;
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
    public boolean getCancelChatReplace() {
        return this.cancelChatReplace;
    }

    public  List<String> getActionList() {
        return this.action;
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
