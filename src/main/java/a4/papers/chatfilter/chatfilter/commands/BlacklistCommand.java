package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BlacklistCommand implements CommandExecutor {

    ChatFilter chatFilter;

    public BlacklistCommand(ChatFilter instance) {
        chatFilter = instance;
    }

    public static boolean useLoop(String[] arr, String targetValue) {
        for (String s : arr) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
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
        switch (args[1].toLowerCase()) {
            case "list":
                if (args.length == 2) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ARGS_LIST.s)));
                    return true;

                }
                if (args[2].equals("ip")) {
                    List<String> strlist = new ArrayList<>();
                    ComponentBuilder message = new ComponentBuilder("");
                    for (String words : chatFilter.regexAdvert.keySet()) {
                        strlist.add(chatFilter.regexAdvert.get(words).getWord());
                    }
                    Collections.sort(strlist);
                    for (String word : strlist) {
                        message.append(ChatColor.WHITE + " " + word + ", ");
                        message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cf blacklist remove ip " + word));
                        message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_IP_1.s).replace("%ip%", word)))));
                    }
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_IP_2.s)));
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_IP_3.s)));
                    sender.spigot().sendMessage(message.create());
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST_IP_4.s)));
                    return true;
                }
                if (args[2].equals("word")) {
                    List<String> strlist = new ArrayList<>();
                    for (String stringlist : chatFilter.regexWords.keySet()) {
                        strlist.add(chatFilter.regexWords.get(stringlist).getWord());
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
                break;
            case "remove":
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
                    ConfigurationSection config = chatFilter.getWordConfig().getConfigurationSection("ChatFilter");
                    Set<String> set = config.getKeys(false);
                    if (!set.contains(ArgsString)) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_WORD_NO.s).replace("%word%", ArgsString)));
                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_WORD_REMOVED.s).replace("%word%", ArgsString)));
                        chatFilter.regexWords.remove(ArgsString);
                        config.set(ArgsString, null);
                        chatFilter.save();
                        chatFilter.getFilters().reloadFilters();
                    }
                    return true;
                }
                if (args[2].equals("ip") && args.length > 3) {
                    ConfigurationSection config = chatFilter.getAdvertConfig().getConfigurationSection("ChatFilter");
                    Set<String> set = config.getKeys(false);
                    if (!set.contains(args[3])) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_IP_NO.s).replace("%ip%", args[3])));
                        return true;
                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_IP_REMOVED.s).replace("%ip%", args[3])));
                        chatFilter.regexWords.remove(args[3].toLowerCase());
                        config.set(args[3].toLowerCase(), null);
                        chatFilter.save();
                        chatFilter.getFilters().reloadFilters();
                        return true;
                    }
                }
                break;
            case "add":
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
                    String argsString = String.join(" ", args).toLowerCase().replace("blacklist add word ", "");
                    if (matchStringAdd(argsString)) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ADD_WORD_NO.s).replace("%word%", argsString)));
                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ADD_WORD_ADDED.s).replace("%word%", argsString)));
                        chatFilter.getFilters().createWordFilter(argsString, sender.getName());
                    }
                } else if (args[2].equals("ip") && args.length > 3) {
                    String argsString = String.join(" ", args).toLowerCase().replaceAll("blacklist add ip ", "");
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ADD_IP_ADDED.s).replace("%ip%", argsString)));
                    chatFilter.getFilters().createAdvertFilter(argsString, sender.getName());
                    return true;
                }
                break;
            default:
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_ARGS.s)));
                return true;
        }
        return false;
    }

    private boolean matchStringAdd(String args) {
        ConfigurationSection config = chatFilter.getWordConfig().getConfigurationSection("ChatFilter");
        for (String s : config.getKeys(false)) {
            if (s.replace("#", "").equals(args.replace("#", ""))) {
                return true;
            }
        }
        return false;
    }
    private boolean matchStringRemove(String args) {
        ConfigurationSection config = chatFilter.getWordConfig().getConfigurationSection("ChatFilter");
        for (String s : config.getKeys(false)) {
            if (s.equals(args)) {
                return true;
            }
        }
        return false;
    }

}
