package a4.papers.chatfilter.chatfilter.events;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Result;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.EventExecutor;

public class SignListener implements EventExecutor, Listener {

    ChatFilter chatFilter;

    public SignListener(ChatFilter instance) {
        chatFilter = instance;
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onSignEvent((SignChangeEvent) event);
    }

    public void onSignEvent(SignChangeEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("chatfilter.bypass") || p.hasPermission("chatfilter.bypass.sign")) {
            return;
        }
        String prefix = "";
        String warnPlayerMessage = "";

        String line0 = ChatColor.stripColor(event.getLine(0).toLowerCase());
        String line1 = ChatColor.stripColor(event.getLine(1).toLowerCase());
        String line2 = ChatColor.stripColor(event.getLine(2).toLowerCase());
        String line3 = ChatColor.stripColor(event.getLine(3).toLowerCase());
        String lines = line0 + " " + line1 + " " + line2 + " " + line3;

        Result result = chatFilter.getChatFilters().validResult(lines, p);
        if (result.getResult()) {
            Types type = result.getType();
            String[] stringArray = result.getStringArray();
            FilterWrapper filterWrapper = result.getFilterWrapper();
            chatFilter.commandHandler.runCommand(p, stringArray, filterWrapper);
            switch (type) {
                case SWEAR:
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixSignSwear.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    break;
                case IP_DNS:
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixSignIP.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    break;
                case IP_SWEAR:
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixSignIPandSwear.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    break;
                case FONT:
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixSignFont.s).replace("%player%", p.getName());
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnFontMessage.s);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
            event.getBlock().breakNaturally();
            if (filterWrapper.getLogToConsole())
                chatFilter.sendConsole(type, lines, p, filterWrapper.getRegex(), "Sign");
            if (filterWrapper.getWarnPlayer())
                p.sendMessage(chatFilter.colour(warnPlayerMessage));
            if (filterWrapper.getSendStaff()) {
                chatFilter.sendStaffMessage(chatFilter.colour(prefix));
                if (!event.getLine(0).isEmpty()) {
                    for (String oneWord : stringArray) {
                        line0 = line0.replace(oneWord.replace(" ",""), chatFilter.colour(chatFilter.settingsSwearHighLight.replace("%catch%", oneWord)));
                    }
                    chatFilter.sendStaffMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.signLine1.s)) + line0);
                }
                if (!event.getLine(1).isEmpty()) {
                    for (String oneWord : stringArray) {
                        line1 = line1.replace(oneWord.replace(" ",""), chatFilter.colour(chatFilter.settingsSwearHighLight.replace("%catch%", oneWord)));
                    }
                    chatFilter.sendStaffMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.signLine2.s)) + line1);
                }
                if (!event.getLine(2).isEmpty()) {
                    for (String oneWord : stringArray) {
                        line2 = line2.replace(oneWord.replace(" ",""), chatFilter.colour(chatFilter.settingsSwearHighLight.replace("%catch%", oneWord)));
                    }
                    chatFilter.sendStaffMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.signLine3.s)) + line2);
                }
                if (!event.getLine(3).isEmpty()) {
                    for (String oneWord : stringArray) {
                        line3 = line3.replace(oneWord.replace(" ",""), chatFilter.colour(chatFilter.settingsSwearHighLight.replace("%catch%", oneWord)));
                    }
                    chatFilter.sendStaffMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.signLine4.s)) + line3);
                }
            }
        }
    }
}

