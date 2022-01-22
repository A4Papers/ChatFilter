package a4.papers.chatfilter.chatfilter.events;


import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import a4.papers.chatfilter.chatfilter.shared.Types;
import org.apache.commons.lang.StringUtils;
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
            String regexPattern = chatFilter.getChatFilters().validResult(chatMessage, p).getRegexPattern();

            chatFilter.commandHandler.runCommand(type, p, stringArray);
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
            chatFilter.sendConsole(type, chatMessage, p, regexPattern, "Chat");
            p.sendMessage(chatFilter.colour(warnPlayerMessage));
            for (String oneWord : chatFilter.getChatFilters().validResult(chatMessage, p).getStringArray()) {
                chatMessage = chatMessage.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
            }
            chatFilter.sendStaffMessage(chatFilter.colour(prefix + chatMessage));
            if (chatFilter.cancelChat) {
                event.setCancelled(true);
            } else {
                String msg = event.getMessage();
                for (String oneWord : chatFilter.getChatFilters().validResult(msg, p).getStringArray()) {
                    msg = msg.replace(oneWord, StringUtils.repeat(chatFilter.cancelChatReplace, oneWord.length()));
                }
                event.setMessage(msg);
            }
        }
    }
}
