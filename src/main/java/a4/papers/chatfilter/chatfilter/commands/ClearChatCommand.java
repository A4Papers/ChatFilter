package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import a4.papers.chatfilter.chatfilter.shared.lang.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {

    ChatFilter chatFilter;

    public ClearChatCommand(ChatFilter instance) {
        chatFilter = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        clearChat(sender);
        return false;
    }

    public boolean clearChat(CommandSender sender) {
        if (!sender.hasPermission("chatfilter.clear")) {
            sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.NO_PERMISSION.s)));
        } else if (sender.hasPermission("chatfilter.clear")) {
            for (Player noPermissionPlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (!noPermissionPlayer.hasPermission("chatfilter.bypass"))
                    for (int i = 0; i < 100; i++) {
                        noPermissionPlayer.sendMessage(" ");
                    }
            }
            for (Player PermissionPlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (PermissionPlayer.hasPermission("chatfilter.bypass") || PermissionPlayer.hasPermission("chatfilter.view"))
                    PermissionPlayer.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.clearChatMessage.s).replace("%player%", sender.getName())));
            }
        }
        return false;
    }
}
