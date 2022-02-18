package a4.papers.chatfilter.chatfilter.events;


import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Result;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

public class SwearChatListener implements EventExecutor, Listener {

    ChatFilter chatFilter;

    public SwearChatListener(ChatFilter instance) {
        chatFilter = instance;
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onPlayerSwear((AsyncPlayerChatEvent) event);
    }


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
        Result result = chatFilter.getChatFilters().validResult(chatMessage, p);
        if (result.getResult()) {
            Types type = result.getType();
            String[] stringArray = result.getStringArray();
            FilterWrapper filterWrapper = result.getFilterWrapper();
            chatFilter.commandHandler.runCommand(p, stringArray, filterWrapper);
            switch (type) {
                case SWEAR:
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixChatSwear.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    break;
                case IP_DNS:
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixChatIP.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    break;
                case IP_SWEAR:
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixChatIPandSwear.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    break;
                case FONT:
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixChatFont.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnFontMessage.s);
                    break;
                case URL:
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixChatIP.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnURLMessage.s);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
            if (filterWrapper.getLogToConsole())
                chatFilter.sendConsole(type, chatMessage, p, filterWrapper.getRegex(), "Chat");
            if (filterWrapper.getWarnPlayer())
                p.sendMessage(chatFilter.colour(warnPlayerMessage));
            if (filterWrapper.getSendStaff()) {
                for (String oneWord : stringArray) {
                    chatMessage = chatMessage.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight.replace("%catch%", oneWord)));
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
