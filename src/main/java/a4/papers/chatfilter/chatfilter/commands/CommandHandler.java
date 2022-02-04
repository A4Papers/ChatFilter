package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandHandler {
    ChatFilter chatFilter;

    public CommandHandler(ChatFilter instance) {
        chatFilter = instance;
    }

    public void runCommand(Player p, String[] word, FilterWrapper filterWrapper) {
        if (word != null && word.length > 0) {
            String firstWord = word[0];
            if (filterWrapper.getActionList() != null) {
                for (String s : filterWrapper.getActionList()) {
                    if (s.contains("<SendMessage>")) {
                        p.sendMessage(chatFilter.colour(s.replace("<SendMessage>", "").replace("%player%", p.getName()).replace("%item%", firstWord)));
                    }
                    if (s.contains("<SendCommand>")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), s.replace("<SendCommand>", "").replace("%player%", p.getName()).replace("%item%", firstWord));
                            }
                        }.runTask(chatFilter);
                    }
                }
            }
        }
    }
}
