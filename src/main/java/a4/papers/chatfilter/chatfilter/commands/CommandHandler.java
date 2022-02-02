package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Types;
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
            if (filterWrapper.getCommand() != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), filterWrapper.getCommand().replace("%player%", p.getName()).replace("%placeholder%", firstWord));
                    }
                }.runTask(chatFilter);
            }
        }
    }
}
