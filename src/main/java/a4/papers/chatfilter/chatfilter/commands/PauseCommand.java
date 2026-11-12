package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
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
            sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.NO_PERMISSION.s)));
            return true;
        }
        boolean pausing = !chatFilter.chatPause;
        String message = chatFilter.colour(chatFilter.getLang().mapToString(pausing ? EnumStrings.PAUSE_CHAT.s : EnumStrings.UNPAUSE_CHAT.s).replace("%player%", sender.getName()));
        for (Player permplayer : Bukkit.getServer().getOnlinePlayers()) {
            if (permplayer.hasPermission("chatfilter.bypass") || permplayer.hasPermission("chatfilter.pause") || permplayer.hasPermission("chatfilter.view")) {
                permplayer.sendMessage(message);
            }
        }
        chatFilter.chatPause = pausing;
        return false;
    }

}
