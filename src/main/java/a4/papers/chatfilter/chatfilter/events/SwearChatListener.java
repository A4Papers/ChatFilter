package a4.papers.chatfilter.chatfilter.events;


import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SwearChatListener implements Listener {

    ChatFilter chatFilter;

    public SwearChatListener(ChatFilter instance) {
        chatFilter = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSwear(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        String chatMessage = ChatColor.stripColor(event.getMessage()).toLowerCase();
        String prefix = "";
        String warnPlayerMessage = "";
        if (p.hasPermission("chatfilter.bypass") || p.hasPermission("chatfilter.bypass.chat"))
            return;
        if (event.isCancelled())
            return;
        if (chatFilter.chatPause)
            return;
        if (chatFilter.getChatFilters().validResult(chatMessage, p).getResult()) {

            Types type = chatFilter.getChatFilters().validResult(chatMessage, p).getType();
            String[] stringArray = chatFilter.getChatFilters().validResult(chatMessage, p).getStringArray();
            FilterWrapper filterWrapper = chatFilter.getChatFilters().validResult(chatMessage, p).getFilterWrapper();

            chatFilter.commandHandler.runCommand(p, chatFilter.getChatFilters().validResult(chatMessage, p).getStringArray(), filterWrapper);

            if (type == Types.SWEAR) {
                prefix = chatFilter.getLang().mapToString(EnumStrings.prefixChatSwear.s).replace("%player%", p.getName());
                warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
            }
            if (type == Types.IP_DNS) {
                prefix = chatFilter.getLang().mapToString(EnumStrings.prefixChatIP.s).replace("%player%", p.getName());
                warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
            }
            if (type == Types.IP_SWEAR) {
                prefix = chatFilter.getLang().mapToString(EnumStrings.prefixChatIPandSwear.s).replace("%player%", p.getName());
                warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
            }
            if (type == Types.FONT) {
                prefix = chatFilter.getLang().mapToString(EnumStrings.prefixChatFont.s).replace("%player%", p.getName());
                warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnFontMessage.s);
            }
            if (type == Types.URL) {
                prefix = chatFilter.getLang().mapToString(EnumStrings.prefixChatIP.s).replace("%player%", p.getName());
                warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnURLMessage.s);
            }
            if (filterWrapper.getLogToConsole()) {
                chatFilter.sendConsole(type, chatMessage, p, filterWrapper.getRegex(), "Chat");
            }
            if (filterWrapper.getWarnPlayer()) {
                p.sendMessage(chatFilter.colour(warnPlayerMessage));
            }
            if (filterWrapper.getSendStaff()) {
                for (String oneWord : chatFilter.getChatFilters().validResult(chatMessage, p).getStringArray()) {
                    chatMessage = chatMessage.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight.replace("%catch%",oneWord)));
                }
                chatFilter.sendStaffMessage(chatFilter.colour(prefix + chatMessage));
            }
            if (filterWrapper.getCancelChat()) {
                event.setCancelled(true);
            } else {
                String msg = event.getMessage();
                for (String oneWord : stringArray) {
                    if (filterWrapper.getCancelChatReplace()) {
                        msg = msg.replace(oneWord, filterWrapper.getReplace());
                    }
                }
                event.setMessage(msg);
            }
        }
    }
}
