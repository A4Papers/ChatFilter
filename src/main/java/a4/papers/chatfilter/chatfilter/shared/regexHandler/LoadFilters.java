package a4.papers.chatfilter.chatfilter.shared.regexHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.CompiledFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.UnicodeWrapper;

public class LoadFilters {

    ChatFilter chatFilter;

    public LoadFilters(ChatFilter instance) {
        chatFilter = instance;
    }

    public void loadWordFilter() {
        ConfigurationSection section = chatFilter.getWordConfig().getConfigurationSection("ChatFilter");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection word = section.getConfigurationSection(key);
            if (word == null) {
                continue;
            }
            boolean informConsole = word.getBoolean("Warn.Console");
            boolean msgToStaff = word.getBoolean("Warn.Staff");
            boolean msgToPlayer = word.getBoolean("Warn.Player");
            boolean cancelChat = word.getBoolean("CancelChat.Cancel");
            boolean cancelChatReplace = word.getBoolean("CancelChat.Replace");
            String replaceWith = word.getString("CancelChat.ReplaceWith");
            List<String> command = word.getStringList("Action");
            boolean enabled = word.getBoolean("Enabled");
            if (!enabled) {
                continue;
            }
            List<String> regexList = word.getStringList("Regex");
            if (regexList.isEmpty()) {
                String regex = word.getString("Regex");
                chatFilter.regexWords.put(regex, new FilterWrapper(key, command, regex, cancelChat, cancelChatReplace, replaceWith, msgToStaff, informConsole, msgToPlayer));
            } else {
                for (int i = 0, size = regexList.size(); i < size; i++) {
                    String regex = regexList.get(i);
                    chatFilter.regexWords.put(regex, new FilterWrapper(key, command, regex, cancelChat, cancelChatReplace, replaceWith, msgToStaff, informConsole, msgToPlayer));
                }
            }
        }
    }

    public void loadAdvertFilter() {
        ConfigurationSection section = chatFilter.getAdvertConfig().getConfigurationSection("ChatFilter");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection word = section.getConfigurationSection(key);
            if (word == null) {
                continue;
            }
            boolean informConsole = word.getBoolean("Warn.Console");
            boolean msgToStaff = word.getBoolean("Warn.Staff");
            boolean msgToPlayer = word.getBoolean("Warn.Player");
            boolean cancelChat = word.getBoolean("CancelChat.Cancel");
            boolean cancelChatReplace = word.getBoolean("CancelChat.Replace");
            String replaceWith = word.getString("CancelChat.ReplaceWith");
            List<String> command = word.getStringList("Action");
            boolean enabled = word.getBoolean("Enabled");
            if (!enabled) {
                continue;
            }
            List<String> regexList = word.getStringList("Regex");
            if (regexList.isEmpty()) {
                String regex = word.getString("Regex");
                chatFilter.regexAdvert.put(regex, new FilterWrapper(key, command, regex, cancelChat, cancelChatReplace, replaceWith, msgToStaff, informConsole, msgToPlayer));
            } else {
                for (int i = 0, size = regexList.size(); i < size; i++) {
                    String regex = regexList.get(i);
                    chatFilter.regexAdvert.put(regex, new FilterWrapper(key, command, regex, cancelChat, cancelChatReplace, replaceWith, msgToStaff, informConsole, msgToPlayer));
                }
            }
        }
    }

    public void loadUnicodeFilter() {
        loadUnicodeWhitelist();
        ConfigurationSection section = chatFilter.getUnicodeConfig().getConfigurationSection("Unicode.blacklist");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection word = section.getConfigurationSection(key);
                if (word == null) {
                    continue;
                }
                String start = word.getString("range-start");
                String end = word.getString("range-end");
                chatFilter.unicodeBlacklist.put(key, new UnicodeWrapper(start, end));
            }
        }
        rebuildUnicodeRanges();
    }

    private void rebuildUnicodeRanges() {
        Map<String, UnicodeWrapper> blacklist = chatFilter.unicodeBlacklist;
        int size = blacklist.size();
        int[] ranges = new int[size << 1];
        int index = 0;
        for (UnicodeWrapper wrapper : blacklist.values()) {
            ranges[index++] = wrapper.getStartInt();
            ranges[index++] = wrapper.getEndInt();
        }
        chatFilter.unicodeRanges = ranges;
    }

    public void loadUnicodeWhitelist() {
        chatFilter.unicodeWhitelist.clear();
        if (chatFilter.getUnicodeConfig().getStringList("Unicode.whitelist").isEmpty()) {
            String s = chatFilter.getUnicodeConfig().getString("Unicode.whitelist");
            chatFilter.unicodeWhitelist.add(s);
        } else {
            List<String> whitelist = chatFilter.getUnicodeConfig().getStringList("Unicode.whitelist");
            chatFilter.unicodeWhitelist.addAll(whitelist);
        }
    }

    public void createWordFilter(String word, String sender) {
        writeWordFilter(word, sender);
        chatFilter.save();
        reloadFilters();
    }

    public void importWordFilters(List<String> words, String sender) {
        for (int i = 0, size = words.size(); i < size; i++) {
            writeWordFilter(words.get(i), sender);
        }
        chatFilter.save();
        reloadFilters();
    }

    private void writeWordFilter(String word, String sender) {
        String regex = chatFilter.regexpGenerator().generateRegexp(word);
        if (chatFilter.perWordOptionsEnable) {
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
        } else {
            ConfigurationSection key = chatFilter.getWordConfig().getConfigurationSection("ChatFilter." + chatFilter.perWordOptionsString);
            if (key == null) {
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString, "");
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Enabled", chatFilter.defaultWordEnabled);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Regex", regex);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Warn.Staff", chatFilter.defaultWordWarnStaff);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Warn.Player", chatFilter.defaultWordWarnPlayer);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Warn.Console", chatFilter.defaultWordWarnConsole);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".CancelChat.Cancel", chatFilter.defaultWordCancelChatCancel);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".CancelChat.Replace", chatFilter.defaultWordCancelReplace);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".CancelChat.ReplaceWith", chatFilter.defaultWordCancelReplaceWith);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Action", chatFilter.defaultWordAction);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".AddedBy", sender);
            } else {
                List<String> regexList = new ArrayList<>(key.getStringList("Regex"));
                regexList.add(regex);
                chatFilter.getWordConfig().set("ChatFilter." + chatFilter.perWordOptionsString + ".Regex", regexList);
            }
        }
    }

    public void createAdvertFilter(String s, String sender) {
        writeAdvertFilter(s, sender);
        chatFilter.save();
        reloadFilters();
    }

    private void writeAdvertFilter(String s, String sender) {
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
    }

    public void reloadFilters() {
        chatFilter.regexAdvert.clear();
        chatFilter.regexWords.clear();
        chatFilter.advertRegexPattern.clear();
        chatFilter.wordRegexPattern.clear();
        loadAdvertFilter();
        loadWordFilter();
        regexCompile();
    }

    public void regexCompile() {
        Map<String, FilterWrapper> adverts = chatFilter.regexAdvert;
        CompiledFilter[] advertCompiled = new CompiledFilter[adverts.size()];
        int i = 0;
        for (Map.Entry<String, FilterWrapper> entry : adverts.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            chatFilter.advertRegexPattern.add(pattern);
            advertCompiled[i++] = new CompiledFilter(pattern, entry.getValue());
        }
        Map<String, FilterWrapper> words = chatFilter.regexWords;
        CompiledFilter[] wordCompiled = new CompiledFilter[words.size()];
        i = 0;
        for (Map.Entry<String, FilterWrapper> entry : words.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            chatFilter.wordRegexPattern.add(pattern);
            wordCompiled[i++] = new CompiledFilter(pattern, entry.getValue());
        }
        chatFilter.advertCompiled = advertCompiled;
        chatFilter.wordCompiled = wordCompiled;
    }
}
