package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    ChatFilter chatFilter;

    public ReloadCommand(ChatFilter instance) {
        chatFilter = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("chatfilter.reload")) {
                try {
                    chatFilter.reloadConfig();
                    chatFilter.saveConfig();
                    chatFilter.byPassWords.clear();
                    chatFilter.byPassDNS.clear();
                    chatFilter.regexWords.clear();
                    chatFilter.regexAdvert.clear();
                    chatFilter.reloadConfigs();
                    chatFilter.getFilters().loadAdvertFilter();
                    chatFilter.getFilters().loadWordFilter();
                    chatFilter.byPassWords = chatFilter.getWhitelistConfig().getStringList("bypassWords");
                    chatFilter.byPassDNS = chatFilter.getWhitelistConfig().getStringList("bypassIP");
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Reload failed.");
                }
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CONFIG_RELOADED.s)));
            } else {
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.NO_PERMISSION.s)));
            }
        }
        return false;
    }
}
