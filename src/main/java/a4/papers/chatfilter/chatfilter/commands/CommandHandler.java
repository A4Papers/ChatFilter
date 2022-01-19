package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.lang.Types;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandHandler {
    ChatFilter chatFilter;

    public CommandHandler(ChatFilter instance) {
        chatFilter = instance;
    }

    public void runCommand(Types type, Player p, String[] word) {
        String firstWord = word[0];
        if (type == Types.IP_DNS && chatFilter.CommandsOnAdvertisesEnabled) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),  chatFilter.getConfig().getString("CommandsOnAdvertises.command").replace("%player%", p.getName()).replace("%placeholder%", firstWord));
                }
            }.runTask(chatFilter);
        }

        if (type == Types.SWEAR && chatFilter.CommandsOnSwearEnabled) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), chatFilter.getConfig().getString("CommandsOnSwear.command").replace("%player%", p.getName()).replace("%placeholder%", firstWord));

                }
            }.runTask(chatFilter);

        }
        if (type == Types.IP_SWEAR && chatFilter.CommandsOnSwearAndAdvertisesEnabled) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),  chatFilter.getConfig().getString("CommandsOnSwearAndAdvertises.command").replace("%player%", p.getName()).replace("%placeholder%", firstWord));
                }
            }.runTask(chatFilter);
        }

        if (type == Types.FONT && chatFilter.CommandsOnFontEnabled) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), chatFilter.getConfig().getString("CommandsOnFont.command").replace("%player%", p.getName()));
                }
            }.runTask(chatFilter);
        }
    }
}
