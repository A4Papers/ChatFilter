package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.lang.EnumStrings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PauseCommand implements CommandExecutor {

    ChatFilter chatFilter;

    public PauseCommand(ChatFilter instance) {
        chatFilter = instance;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("chatfilter.pause")) {
            sender.sendMessage(chatFilter.colour(chatFilter.mapToString(EnumStrings.NO_PERMISSION.s)));
            return true;
        }
        if (sender.hasPermission("chatfilter.pause")) {
            if (!chatFilter.chatPause) {

                for (Player permplayer : Bukkit.getServer().getOnlinePlayers()) {
                    if (permplayer.hasPermission("chatfilter.bypass") || (permplayer.hasPermission("chatfilter.pause")) || permplayer.hasPermission("chatfilter.view"))
                        permplayer.sendMessage(chatFilter.colour(chatFilter.mapToString(EnumStrings.PAUSE_CHAT.s).replace("%player%", sender.getName())));
                }
                chatFilter.chatPause = true;
            } else {
                for (Player permplayer : Bukkit.getServer().getOnlinePlayers()) {
                    if (permplayer.hasPermission("chatfilter.bypass") || (permplayer.hasPermission("chatfilter.pause")) || permplayer.hasPermission("chatfilter.view"))
                        permplayer.sendMessage(chatFilter.colour(chatFilter.mapToString(EnumStrings.UNPAUSE_CHAT.s).replace("%player%", sender.getName())));
                }
                chatFilter.chatPause = false;
            }
        }
        return false;
    }

}
