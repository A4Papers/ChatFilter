package a4.papers.chatfilter.chatfilter.events;


import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
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
        String cmd = ChatColor.stripColor(event.getMessage().toLowerCase());
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
                FilterWrapper filterWrapper = chatFilter.getChatFilters().validResult(cmd, p).getFilterWrapper();
                Types type = chatFilter.getChatFilters().validResult(cmd, p).getType();
                String[] stringArray = chatFilter.getChatFilters().validResult(cmd, p).getStringArray();

                if (type == Types.SWEAR && !swearconfig) {
                    return;
                }
                if (type == Types.IP_DNS && !dnsconfig) {
                    return;
                }
                if (type == Types.SWEAR && swearconfig) {
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixCmdSwear.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                }
                if (type == Types.IP_DNS && dnsconfig) {
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixCmdIP.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                }
                if (type == Types.IP_SWEAR && dnsconfig && !swearconfig) {
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixCmdIP.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                }
                if (type == Types.IP_SWEAR && !dnsconfig && swearconfig) {
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixCmdSwear.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                }
                if (type == Types.IP_SWEAR && dnsconfig && swearconfig) {
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixCmdIPandSwear.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                }
                event.setCancelled(true);
                chatFilter.commandHandler.runCommand(p, stringArray, filterWrapper);
                if (filterWrapper.getLogToConsole()) {
                    chatFilter.sendConsole(type, cmd, p, filterWrapper.getRegex(), "Command");
                }
                if(filterWrapper.getWarnPlayer()) {
                    p.sendMessage(chatFilter.colour(warnPlayerMessage));
                }
                if (filterWrapper.getSendStaff()) {
                    for (String oneWord : stringArray) {
                        cmd = cmd.replace(oneWord, chatFilter.colour( chatFilter.colour(chatFilter.settingsSwearHighLight.replace("%catch%",oneWord))));
                    }
                    chatFilter.sendStaffMessage(chatFilter.colour(prefix + cmd));
                }
            }
        }
    }
}
