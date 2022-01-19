package a4.papers.chatfilter.chatfilter.events;


import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.lang.EnumStrings;
import a4.papers.chatfilter.chatfilter.lang.Types;
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
            chatFilter.commandHandler.runCommand(type, p, chatFilter.getChatFilters().validResult(chatMessage, p).getStringArray());
            if (type == Types.SWEAR) {
                prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixChatSwear.s).replace("%player%", p.getName()));
                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(chatMessage, p).getStringArray()))));
            }
            if (type == Types.IP_DNS) {
                prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixChatIP.s).replace("%player%", p.getName()));
                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(chatMessage, p).getStringArray()))));
            }
            if (type == Types.IP_SWEAR) {
                prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixChatIPandSwear.s).replace("%player%", p.getName()));
                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(chatMessage, p).getStringArray()))));
            }
            if (type == Types.FONT) {
                prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixChatFont.s).replace("%player%", p.getName()));
                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnFontMessage.s));
            }
            if (type == Types.URL) {
                prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixChatIP.s).replace("%player%", p.getName()));
                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnURLMessage.s));
            }
            chatFilter.sendConsole(type, chatMessage, p, chatFilter.getChatFilters().validResult(chatMessage, p).getRegexPattern(), "Chat");
            p.sendMessage(warnPlayerMessage);
            for (String oneWord : chatFilter.getChatFilters().validResult(chatMessage, p).getStringArray()) {
                chatMessage = chatMessage.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
            }
            chatFilter.sendStaffMessage(prefix + chatMessage);
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
