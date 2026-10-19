package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandHandler {
    ChatFilter chatFilter;

    public CommandHandler(ChatFilter instance) {
        chatFilter = instance;
    }

    public void runCommand(Player p, String[] word, FilterWrapper filterWrapper) {
        if (word == null || word.length == 0) {
            return;
        }
        String firstWord = word[0];
        java.util.List<String> actions = filterWrapper.getActionList();
        if (actions == null || actions.isEmpty()) {
            return;
        }
        String playerName = p.getName();
        for (int i = 0, size = actions.size(); i < size; i++) {
            String s = actions.get(i);
            if (s.contains("<SendMessage>")) {
                p.sendMessage(chatFilter.colour(s.replace("<SendMessage>", "").replace("%player%", playerName).replace("%item%", firstWord)));
            }
            if (s.contains("<RunCommand>")) {
                String command = s.replace("<RunCommand>", "").replace("%player%", playerName).replace("%item%", firstWord);
                ChatFilter.runTask(() -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command));
            }
        }
    }
}
