package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlacklistCommand implements CommandExecutor {

    ChatFilter chatFilter;

    public BlacklistCommand(ChatFilter instance) {
        chatFilter = instance;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("chatfilter.blacklist")) {
            sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.NO_PERMISSION.s)));
            return true;
        }

        if (args.length == 1) {
            sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ARGS.s)));
            return true;
        }

        if (args[1].equals("list")) {
            if (args.length == 2) {
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ARGS_LIST.s)));
                return true;

            }
            if (args[2].equals("ip")) {

                List<String> strlist = new ArrayList<>(chatFilter.getConfig().getStringList("filteredIPandDNS"));
                ComponentBuilder message = new ComponentBuilder("");
                for (String words : strlist) {
                    message.append(ChatColor.WHITE + " " + words + ", ");
                    message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cf blacklist remove ip " + words));
                    message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_IP_1.s).replace("%ip%", words)))));
                }
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_IP_2.s)));
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_IP_3.s)));
                sender.spigot().sendMessage(message.create());
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_IP_4.s)));
                return true;
            }
            if (args[2].equals("word")) {
                List<String> strlist = new ArrayList<>();
                for (String stringlist : chatFilter.regExWords) {
                    String listin = stringlist.replace(")\\b", "#").replace("\\b(", "#");
                    String stripRegex = listin.replace("\\", "").replace("(W|d|_)*", "").replace("+", "").replace("(", "").replace(")", "");
                    strlist.add(stripRegex);
                }
                Collections.sort(strlist);
                ComponentBuilder message = new ComponentBuilder("");
                for (String words : strlist) {
                    message.append(ChatColor.WHITE + " " + words + ", ");
                    message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cf blacklist remove word " + words));
                    message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_WORD_1.s).replace("%word%", words)))));
                }
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_WORD_2.s)));
                sender.spigot().sendMessage(message.create());
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_WORD_3.s)));
                return true;
            } else {
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ARGS_LIST.s)));
            }
            return true;
        }
        if (args[1].equals("remove")) {
            if (!sender.hasPermission("chatfilter.blacklist.remove")) {
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.NO_PERMISSION.s)));
                return true;
            }
            if (args.length == 2) {
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_ARG.s)));
                return true;
            }
            if (args[2].equals("word") && args.length == 3) {
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_WORD_ARG.s)));
                return true;
            }
            if (args[2].equals("ip") && args.length == 3) {
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_IP_ARG.s)));
                return true;
            }
            if (args[2].equals("word") && args.length > 3) {
                String ArgsString = String.join(" ", args).toLowerCase().replaceAll("blacklist remove word ", "");
                List<String> alist = new ArrayList<>();
                for (int i = 0; i < ArgsString.length(); i++) {
                    char c = ArgsString.charAt(i);
                    String chars = (new StringBuilder(String.valueOf(c))).toString();
                    if (chars.equals(" ")) {
                        String str = "%SPACE%+(\\W|\\d|_)*";
                        alist.add(str);
                    }
                    if (chars.equals("#")) {
                        String str = "#";
                        alist.add(str);
                    } else {
                        String str = c + "+(\\W|\\d|_)*";
                        alist.add(str);
                    }
                }
                String regexString = alist.toString().replaceAll("\\[|\\]|,|\\s", "").replaceAll("%SPACE%", " ");
                String regexToFilter = regexString;
                if (!regexToFilter.contains("#")) {
                    regexToFilter = "(" + regexString + ")";
                } else if (regexToFilter.startsWith("#") && regexString.endsWith("#")) {
                    regexToFilter = regexToFilter.replace("#", "");
                    regexToFilter = "\\b(" + regexToFilter + ")\\b";
                } else if (regexToFilter.startsWith("#")) {
                    regexToFilter = regexToFilter.replace("#", "\\b(");
                    regexToFilter = regexToFilter + ")";
                } else if (regexString.endsWith("#")) {
                    regexToFilter = regexToFilter.replace("#", ")\\b");
                    regexToFilter = "(" + regexToFilter;
                }
                List<String> list = chatFilter.getConfig().getStringList("filteredWords");
                if (!list.contains(regexToFilter)) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_WORD_NO.s).replace("%word%", ArgsString)));
                } else {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_WORD_REMOVED.s).replace("%word%", ArgsString)));
                    chatFilter.regExWords.remove(regexToFilter);
                    list.remove(regexToFilter);
                    chatFilter.getConfig().set("filteredWords", list);
                    chatFilter.saveConfig();
                    return true;
                }
            }
            if (args[2].equals("ip") && args.length > 3) {
                List<String> list = chatFilter.getConfig().getStringList("filteredIPandDNS");
                if (!list.contains(args[3])) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_IP_NO.s).replace("%ip%", args[3])));
                    return true;
                } else { // CMD_BLACKLIST_REMOVE_IP_REMOVED
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_IP_REMOVED.s).replace("%ip%", args[3])));
                    chatFilter.regExWords.remove(args[3].toLowerCase());
                    list.remove(args[3].toLowerCase());
                    chatFilter.getConfig().set("filteredIPandDNS", list);
                    chatFilter.saveConfig();
                    return true;
                }
            }
        }
        if (args[1].equals("add")) {
            if (args.length == 2) {
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ADD_ARG.s)));

                return true;
            }
            if (args[2].equals("word") && args.length == 3) {
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ADD_WORD_ARG.s)));

                return true;
            }
            if (args[2].equals("ip") && args.length == 3) {
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ADD_IP_ARG.s)));

                return true;
            } else if (args[2].equals("word") && args.length > 3) {
                String ArgsString = String.join(" ", args).toLowerCase().replace("blacklist add word ", "");
                List<String> reglist = new ArrayList<>();
                for (int i = 0; i < ArgsString.length(); i++) {
                    char c = ArgsString.charAt(i);
                    String chars = String.valueOf(c);
                    if (chars.equals(" ")) {
                        String reg = "%SPACE%+(\\W|\\d|_)*";
                        reglist.add(reg);
                    }
                    if (chars.equals("#")) {
                        String reg = "#";
                        reglist.add(reg);
                    } else {
                        String reg = c + "+(\\W|\\d|_)*";
                        reglist.add(reg);
                    }
                }
                String regexString = reglist.toString().replaceAll("\\[|\\]|,|\\s", "").replaceAll("%SPACE%", " ");
                String regexToFilter = regexString;
                if (!regexToFilter.contains("#")) {
                    regexToFilter = "(" + regexString + ")";
                } else if (regexToFilter.startsWith("#") && regexString.endsWith("#")) {
                    regexToFilter = regexToFilter.replace("#", "");
                    regexToFilter = "\\b(" + regexToFilter + ")\\b";
                } else if (regexToFilter.startsWith("#")) {
                    regexToFilter = regexToFilter.replace("#", "\\b(");
                    regexToFilter = regexToFilter + ")";
                } else if (regexString.endsWith("#")) {
                    regexToFilter = regexToFilter.replace("#", ")\\b");
                    regexToFilter = "(" + regexToFilter;
                }
                List<String> list = chatFilter.getConfig().getStringList("filteredWords");
                if (list.contains(regexToFilter)) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ADD_WORD_NO.s).replace("%word%", ArgsString)));

                } else {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ADD_WORD_ADDED.s).replace("%word%", ArgsString)));
                    chatFilter.regExWords.add(regexToFilter);
                    list.add(regexToFilter);
                    chatFilter.getConfig().set("filteredWords", list);
                    chatFilter.saveConfig();
                    return true;
                }
            } else if (args[2].equals("ip") && args.length > 3) {
                String ArgsString = String.join(" ", args).toLowerCase().replaceAll("blacklist add ip ", "");
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ADD_IP_ADDED.s).replace("%ip%", ArgsString)));
                chatFilter.regExDNS.add(ArgsString);
                List<String> list = chatFilter.getConfig().getStringList("filteredIPandDNS");
                list.add(ArgsString);
                chatFilter.getConfig().set("filteredIPandDNS", list);
                chatFilter.saveConfig();
                return true;
            }
        } else {
            sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ARGS.s)));
            return true;
        }
        return false;
    }

}
