package a4.papers.chatfilter.chatfilter.shared.regexHandler;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.regex.Pattern;

public class LoadFilters {

    ChatFilter chatFilter;

    public LoadFilters(ChatFilter instance) {
        chatFilter = instance;
    }

    public void loadWordFilter() {
        for (String key : chatFilter.getWordConfig().getConfigurationSection("ChatFilter").getKeys(false)) {
            ConfigurationSection word = chatFilter.getWordConfig().getConfigurationSection("ChatFilter." + key);
            String regex = word.getString("Regex");
            Boolean informConsole = word.getBoolean("Warn.Console");
            Boolean msgToStaff = word.getBoolean("Warn.Staff");
            Boolean msgToPlayer = word.getBoolean("Warn.Player");
            Boolean cancelChat = word.getBoolean("CancelChat.Cancel");
            Boolean cancelChatReplace = word.getBoolean("CancelChat.Replace");
            String replaceWith = word.getString("CancelChat.ReplaceWith");
            List<String> command = word.getStringList("Action");
            Boolean enabled = word.getBoolean("Enabled");
            if (enabled)
                chatFilter.regexWords.put(regex, new FilterWrapper(key, command, regex, cancelChat, cancelChatReplace, replaceWith, msgToStaff, informConsole, msgToPlayer));
        }
    }

    public void loadAdvertFilter() {
        for (String key : chatFilter.getAdvertConfig().getConfigurationSection("ChatFilter").getKeys(false)) {
            ConfigurationSection word = chatFilter.getAdvertConfig().getConfigurationSection("ChatFilter." + key);
            String regex = word.getString("Regex");
            Boolean informConsole = word.getBoolean("Warn.Console");
            Boolean msgToStaff = word.getBoolean("Warn.Staff");
            Boolean msgToPlayer = word.getBoolean("Warn.Player");
            Boolean cancelChat = word.getBoolean("CancelChat.Cancel");
            Boolean cancelChatReplace = word.getBoolean("CancelChat.Replace");
            String replaceWith = word.getString("CancelChat.ReplaceWith");
            List<String> command = word.getStringList("Action");
            Boolean enabled = word.getBoolean("Enabled");
            if (enabled)
                chatFilter.regexAdvert.put(regex, new FilterWrapper(key, command, regex, cancelChat, cancelChatReplace, replaceWith, msgToStaff, informConsole, msgToPlayer));
        }
    }

    public void createWordFilter(String word, String sender) {
        String regex = chatFilter.regexpGenerator().generateRegexp(word);
        chatFilter.getWordConfig().set("ChatFilter." + word + ".Enabled", chatFilter.defaultWordEnabled);
        chatFilter.getWordConfig().set("ChatFilter." + word + ".Regex", regex);
        chatFilter.getWordConfig().set("ChatFilter." + word + ".Warn.Staff", chatFilter.defaultWordWarnStaff);
        chatFilter.getWordConfig().set("ChatFilter." + word + ".Warn.Player", chatFilter.defaultWordWarnPlayer);
        chatFilter.getWordConfig().set("ChatFilter." + word + ".Warn.Console", chatFilter.defaultWordWarnConsole);
        chatFilter.getWordConfig().set("ChatFilter." + word + ".CancelChat.Cancel", chatFilter.defaultWordCancelChatCancel);
        chatFilter.getWordConfig().set("ChatFilter." + word + ".CancelChat.Replace", chatFilter.defaultWordCancelReplace);
        chatFilter.getWordConfig().set("ChatFilter." + word + ".CancelChat.ReplaceWith", chatFilter.defaultWordCancelReplaceWith);
        chatFilter.getWordConfig().set("ChatFilter." + word + ".Action", chatFilter.defaultWordAction);
        chatFilter.getWordConfig().set("ChatFilter." + word + ".AddedBy", sender);
        chatFilter.save();
        reloadFilters();
        Pattern p = Pattern.compile(regex);
        chatFilter.wordRegexPattern.add(p);
    }

    public void createAdvertFilter(String s, String sender) {
        String notDot = s.replace(".", "");
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Enabled", chatFilter.defaultIPEnabled);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Regex", s);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Warn.Staff", chatFilter.defaultIPWarnStaff);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Warn.Player", chatFilter.defaultIPWarnPlayer);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Warn.Console", chatFilter.defaultIPWarnConsole);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".CancelChat.Cancel", chatFilter.defaultIPCancelChatCancel);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".CancelChat.Replace", chatFilter.defaultIPCancelReplace);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".CancelChat.ReplaceWith", chatFilter.defaultIPCancelReplaceWith);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Action", chatFilter.defaultIPAction);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".AddedBy", sender);
        chatFilter.save();
        reloadFilters();
        Pattern p = Pattern.compile(s);
        chatFilter.advertRegexPattern.add(p);
    }

    public void reloadFilters() {
        chatFilter.regexAdvert.clear();
        chatFilter.regexWords.clear();
        loadAdvertFilter();
        loadWordFilter();
    }
    public void regexCompile() {
        for (String StringMatchedDNS : chatFilter.regexAdvert.keySet()) {
            Pattern p = Pattern.compile(StringMatchedDNS);
            chatFilter.advertRegexPattern.add(p);
        }
        for (String StringMatchedWords : chatFilter.regexWords.keySet()) {
            Pattern p = Pattern.compile(StringMatchedWords);
            chatFilter.wordRegexPattern.add(p);
        }
    }
}
