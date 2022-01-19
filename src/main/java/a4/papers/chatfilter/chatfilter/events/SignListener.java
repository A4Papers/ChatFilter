package a4.papers.chatfilter.chatfilter.events;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.lang.EnumStrings;
import a4.papers.chatfilter.chatfilter.lang.Types;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {

    ChatFilter chatFilter;

    public SignListener(ChatFilter instance) {
        chatFilter = instance;
    }

    @EventHandler
    public void onSignEvent(SignChangeEvent event) {

        Player p = event.getPlayer();

        if (p.hasPermission("chatfilter.bypass") || p.hasPermission("chatfilter.bypass.sign")) {
            return;
        }
        String lines = ChatColor.stripColor(event.getLine(0) + " " + event.getLine(1) + " " + event.getLine(2) + " " + event.getLine(3));
        String prefix = "";
        String warnPlayerMessage = "";

        String line0 = event.getLine(0).toLowerCase();
        String line1 = event.getLine(1).toLowerCase();
        String line2 = event.getLine(2).toLowerCase();
        String line3 = event.getLine(3).toLowerCase();

        if (chatFilter.getChatFilters().validResult(lines, p).getResult()) {
            Types type = chatFilter.getChatFilters().validResult(lines, p).getType();
            event.getBlock().breakNaturally();
            chatFilter.commandHandler.runCommand(type, p, chatFilter.getChatFilters().validResult(lines, p).getStringArray());
            if (type == Types.SWEAR) {
                prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixSignSwear.s).replace("%player%", p.getName()));
                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(lines, p).getStringArray()))));
                if (!event.getLine(0).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line0, p).getStringArray()) {
                        line0 = line0.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(1).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line1, p).getStringArray()) {
                        line1 = line1.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(2).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line2, p).getStringArray()) {
                        line2 = line2.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(3).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line3, p).getStringArray()) {
                        line3 = line3.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
            }
            if (type == Types.IP_DNS) {
                prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixSignIP.s).replace("%player%", p.getName()));
                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(lines, p).getStringArray()))));
                if (!event.getLine(0).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line0, p).getStringArray()) {
                        line0 = line0.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(1).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line1, p).getStringArray()) {
                        line1 = line1.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(2).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line2, p).getStringArray()) {
                        line2 = line2.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(3).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line3, p).getStringArray()) {
                        line3 = line3.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
            }
            if (type == Types.IP_SWEAR) {
                prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixSignIPandSwear.s).replace("%player%", p.getName()));
                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnSwearAndIPMessage.s)).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(lines, p).getStringArray())));
                if (!event.getLine(0).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line0, p).getStringArray()) {
                        line0 = line0.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(1).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line1, p).getStringArray()) {
                        line1 = line1.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(2).isEmpty()) {

                    for (String oneWord : chatFilter.getChatFilters().validResult(line2, p).getStringArray()) {
                        line2 = line2.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(3).isEmpty()) {

                    for (String oneWord : chatFilter.getChatFilters().validResult(line3, p).getStringArray()) {
                        line3 = line3.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
            }
            if (type == Types.FONT) {
                prefix = chatFilter.colour(chatFilter.mapToString(EnumStrings.prefixSignFont.s).replace("%player%", p.getName()));
                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(EnumStrings.warnFontMessage.s));
                if (!event.getLine(0).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line0, p).getStringArray()) {
                        line0 = line0.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(1).isEmpty()) {
                    for (String oneWord : chatFilter.getChatFilters().validResult(line1, p).getStringArray()) {
                        line1 = line1.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(2).isEmpty()) {

                    for (String oneWord : chatFilter.getChatFilters().validResult(line2, p).getStringArray()) {
                        line2 = line2.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
                if (!event.getLine(3).isEmpty()) {

                    for (String oneWord : chatFilter.getChatFilters().validResult(line3, p).getStringArray()) {
                        line3 = line3.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                    }
                }
            }
            chatFilter.sendConsole(chatFilter.getChatFilters().validResult(lines, p).getType(), lines, p, chatFilter.getChatFilters().validResult(lines, p).getRegexPattern(), "Sign");
            p.sendMessage(warnPlayerMessage);
            chatFilter.sendStaffMessage(prefix);
            if (!line0.isEmpty()) {
                chatFilter.sendStaffMessage(chatFilter.colour(chatFilter.mapToString(EnumStrings.signLine1.s)) + line0);
            }
            if (!line1.isEmpty()) {
                chatFilter.sendStaffMessage(chatFilter.colour(chatFilter.mapToString(EnumStrings.signLine2.s)) + line1);
            }
            if (!line2.isEmpty()) {
                chatFilter.sendStaffMessage(chatFilter.colour(chatFilter.mapToString(EnumStrings.signLine3.s)) + line2);
            }
            if (!line3.isEmpty()) {
                chatFilter.sendStaffMessage(chatFilter.colour(chatFilter.mapToString(EnumStrings.signLine4.s)) + line3);
            }
        }
    }
}

