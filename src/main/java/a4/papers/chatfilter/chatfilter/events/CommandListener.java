package a4.papers.chatfilter.chatfilter.events;


import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.lang.EnumStrings;
import a4.papers.chatfilter.chatfilter.lang.Types;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    ChatFilter chatFilter;

    public CommandListener(ChatFilter instance) {
        chatFilter = instance;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        String cmd = event.getMessage().toLowerCase();
        String[] array = cmd.split(" ");
        if (p.hasPermission("chatfilter.bypass") || p.hasPermission("chatfilter.bypass.command"))
            return;
        if (event.isCancelled())
            return;
        if (!chatFilter.cmdCheck)
            return;
        if (chatFilter.getConfig().getConfigurationSection("commands").getKeys(false).contains(array[0].replace("/", ""))) {
            boolean swearconfig = chatFilter.getConfig().getBoolean("commands." + array[0].replace("/", "") + ".swear");
            boolean dnsconfig = chatFilter.getConfig().getBoolean("commands." + array[0].replace("/", "") + ".ip");
            String prefix = "Error";
            String warnPlayerMessage = "Error";
            if (chatFilter.getChatFilters().validResult(cmd, p).getResult()) {
                Types type = chatFilter.getChatFilters().validResult(cmd, p).getType();
                if (type == Types.SWEAR && !swearconfig) {
                    return;
                }
                if (type == Types.IP_DNS && !dnsconfig) {
                    return;
                }
                if (type == Types.SWEAR && swearconfig) {
                    prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixCmdSwear.s).replace("%player%", p.getName()));
                    warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(cmd, p).getStringArray()))));
                }
                if (type == Types.IP_DNS && dnsconfig) {
                    prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixCmdIP.s).replace("%player%", p.getName()));
                    warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(cmd, p).getStringArray()))));
                }
                if (type == Types.IP_SWEAR && dnsconfig && !swearconfig) {
                    prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixCmdIP.s).replace("%player%", p.getName()));
                    warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(cmd, p).getStringArray()))));
                }
                if (type == Types.IP_SWEAR && !dnsconfig && swearconfig) {
                    prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixCmdSwear.s).replace("%player%", p.getName()));
                    warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(cmd, p).getStringArray()))));
                }
                if (type == Types.IP_SWEAR && dnsconfig && swearconfig) {
                    prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixCmdIPandSwear.s).replace("%player%", p.getName()));
                    warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(cmd, p).getStringArray()))));
                }
                event.setCancelled(true);
                chatFilter.commandHandler.runCommand(type, p, chatFilter.getChatFilters().validResult(cmd, p).getStringArray());
                chatFilter.sendConsole(chatFilter.getChatFilters().validResult(cmd, p).getType(), cmd, p, chatFilter.getChatFilters().validResult(cmd, p).getRegexPattern(), "Command");
                p.sendMessage(warnPlayerMessage);
                for (String oneWord : chatFilter.getChatFilters().validResult(cmd, p).getStringArray()) {
                    cmd = cmd.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                }
                chatFilter.sendStaffMessage(prefix + cmd);
            }
        }
    }
}
