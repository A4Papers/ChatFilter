package a4.papers.chatfilter.chatfilter.shared.regexHandler;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

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

            Boolean cancelChat = word.getBoolean("CancelChat");
            String replaceWith = word.getString("ReplaceWith");
            String command = word.getString("Command");
            Boolean enabled = word.getBoolean("Enabled");
            if (enabled)
                chatFilter.regexWords.put(regex, new FilterWrapper(key, command, regex, cancelChat, replaceWith, msgToStaff, informConsole, msgToPlayer));
        }
    }

    public void loadAdvertFilter() {
        for (String key : chatFilter.getAdvertConfig().getConfigurationSection("ChatFilter").getKeys(false)) {
            ConfigurationSection word = chatFilter.getAdvertConfig().getConfigurationSection("ChatFilter." + key);
            String regex = word.getString("Regex");
            Boolean informConsole = word.getBoolean("Warn.Console");
            Boolean msgToStaff = word.getBoolean("Warn.Staff");
            Boolean msgToPlayer = word.getBoolean("Warn.Player");

            Boolean cancelChat = word.getBoolean("CancelChat");
            String replaceWith = word.getString("ReplaceWith");
            String command = word.getString("Command");
            Boolean enabled = word.getBoolean("Enabled");
            if (enabled)
                chatFilter.regexAdvert.put(regex, new FilterWrapper(key, command, regex, cancelChat, replaceWith, msgToStaff, informConsole, msgToPlayer));
        }
    }

    public void createWordFilter(String s, CommandSender sender) {
        chatFilter.getWordConfig().set("ChatFilter." + s + ".Enabled", true);
        chatFilter.getWordConfig().set("ChatFilter." + s + ".Warn.Staff", true);
        chatFilter.getWordConfig().set("ChatFilter." + s + ".Warn.Player", true);
        chatFilter.getWordConfig().set("ChatFilter." + s + ".Warn.Console", true);
        chatFilter.getWordConfig().set("ChatFilter." + s + ".Regex", RegexpGenerator.generateRegexp(s));
        chatFilter.getWordConfig().set("ChatFilter." + s + ".CancelChat", true);
        chatFilter.getWordConfig().set("ChatFilter." + s + ".ReplaceWith", "no");
        chatFilter.getWordConfig().set("ChatFilter." + s + ".Command", "no command");
        chatFilter.getWordConfig().set("ChatFilter." + s + ".AddedBy", sender.getName());
        chatFilter.save();
        reload();
    }

    public void createAdvertFilter(String s, CommandSender sender) {
        String notDot = s.replace(".","");
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Enabled", true);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Warn.Staff", true);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Warn.Player", true);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Warn.Console", true);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Regex",s);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".CancelChat", true);
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".ReplaceWith", "no");
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".Command", "no command");
        chatFilter.getAdvertConfig().set("ChatFilter." + notDot + ".AddedBy", sender.getName());
        chatFilter.save();
        reload();
    }

    public void reload() {
        chatFilter.regexAdvert.clear();
        chatFilter.regexWords.clear();
        loadAdvertFilter();
        loadWordFilter();
    }

}
