package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
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
            return false;
        }
        String clearMessage = chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.clearChatMessage.s).replace("%player%", sender.getName()));
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            boolean bypass = onlinePlayer.hasPermission("chatfilter.bypass");
            if (!bypass) {
                for (int i = 0; i < 100; i++) {
                    onlinePlayer.sendMessage(" ");
                }
            }
            if (bypass || onlinePlayer.hasPermission("chatfilter.view")) {
                onlinePlayer.sendMessage(clearMessage);
            }
        }
        return false;
    }
}
