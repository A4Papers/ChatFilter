package a4.papers.chatfilter.chatfilter.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class WhitelistCommand implements CommandExecutor {

    ChatFilter chatFilter;

    public WhitelistCommand(ChatFilter instance) {
        chatFilter = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("chatfilter.whitelist")) {
            sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.NO_PERMISSION.s)));
            return true;
        }
        if (args.length == 1) {
            sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ARGS.s)));
            return true;
        }
        switch (args[1]) {
            case "list":
                if (args.length == 2) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ARGS_LIST.s)));
                    return true;
                }
                if (args[2].equals("word")) {
                    List<String> words = new ArrayList<>(chatFilter.byPassWords);
                    Collections.sort(words);
                    if (chatFilter.manager.supported("text-component")) {
                        ComponentBuilder message = new ComponentBuilder("");
                        for (String word : words) {
                            message.append(ChatColor.WHITE + " " + word + ", ");
                            message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cf whitelist remove word " + word));
                            message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_WORD_1.s).replace("%word%", word)))));
                        }
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_WORD_2.s)));
                        sender.spigot().sendMessage(message.create());
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_WORD_3.s)));
                        return true;
                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_WORD_2.s)));
                        sender.sendMessage(String.join(", ", words));
                        return true;

                    }
                }
                if (args[2].equals("ip")) {
                    List<String> words = new ArrayList<>(chatFilter.byPassDNS);
                    Collections.sort(words);
                    if (chatFilter.manager.supported("text-component")) {

                        ComponentBuilder message = new ComponentBuilder("");
                        for (String word : words) {
                            message.append(ChatColor.WHITE + " " + word + ", ");
                            message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cf whitelist remove ip " + word));
                            message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_WORD_1.s).replace("%ip%", word)))));
                        }
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_IP_2.s)));
                        sender.spigot().sendMessage(message.create());
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_IP_3.s)));
                        return true;
                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_IP_2.s)));
                        sender.sendMessage(String.join(", ", words));
                    }
                }
                break;
            case "add":
                if (args.length == 2) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_ARG.s)));
                    return true;
                }
                if (args[2].equals("word") && args.length == 3) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_WORD_ARG.s)));
                    return true;
                }
                if (args[2].equals("ip") && args.length == 3) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_IP_ARG.s)));
                    return true;
                }
                if (args[2].equals("ip") && args.length > 3) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        if (i > 3) sb.append(' ');
                        sb.append(args[i]);
                    }
                    String ArgsString = sb.toString().toLowerCase();
                    List<String> list = chatFilter.getWhitelistConfig().getStringList("bypassIP");
                    if (list.contains(ArgsString)) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_IP_NO.s).replace("%ip%", ArgsString)));
                        return true;
                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_IP_ADDED.s).replace("%ip%", ArgsString)));
                        chatFilter.byPassDNS.add(ArgsString);
                        list.add(ArgsString);
                        chatFilter.getWhitelistConfig().set("bypassIP", list);
                        chatFilter.rebuildBypassReplacements();
                        chatFilter.saveWhitelist();
                        return true;
                    }
                }
                if (args[2].equals("word") && args.length > 3) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        if (i > 3) sb.append(' ');
                        sb.append(args[i]);
                    }
                    String ArgsString = sb.toString().toLowerCase();
                    List<String> list = chatFilter.getWhitelistConfig().getStringList("bypassWords");
                    if (list.contains(ArgsString)) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_WORD_NO.s).replace("%word%", ArgsString)));
                        return true;

                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_WORD_ADDED.s).replace("%word%", ArgsString)));
                        chatFilter.byPassWords.add(ArgsString);
                        list.add(ArgsString);
                        chatFilter.getWhitelistConfig().set("bypassWords", list);
                        chatFilter.rebuildBypassReplacements();
                        chatFilter.saveWhitelist();
                        return true;
                    }
                }
                break;
            case "remove":
                if (args.length == 2) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE_ARG.s)));
                    return true;
                }
                if (args[2].equals("word") && args.length == 3) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE_WORD_ARG.s)));
                    return true;
                }
                if (args[2].equals("ip") && args.length == 3) {
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE_IP_ARG.s)));
                    return true;
                }
                if (args[2].equals("word") && args.length > 3) {
                    if (!sender.hasPermission("chatfilter.whitelist.remove")) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.NO_PERMISSION.s)));
                        return true;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        if (i > 3) sb.append(' ');
                        sb.append(args[i]);
                    }
                    String ArgsString = sb.toString().toLowerCase();
                    List<String> list = chatFilter.getWhitelistConfig().getStringList("bypassWords");
                    if (!list.contains(ArgsString)) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE_WORD_NO.s).replace("%word%", ArgsString)));
                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE_WORD_REMOVED.s).replace("%word%", ArgsString)));
                        chatFilter.byPassWords.remove(ArgsString);
                        list.remove(ArgsString);
                        chatFilter.getWhitelistConfig().set("bypassWords", list);
                        chatFilter.rebuildBypassReplacements();
                        chatFilter.saveWhitelist();
                        return true;
                    }
                }
                if (args[2].equals("ip") && args.length > 3) {
                    List<String> list = chatFilter.getWhitelistConfig().getStringList("bypassIP");
                    if (!list.contains(args[3])) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE_IP_NO.s).replace("%ip%", args[3])));
                        return true;
                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE_IP_REMOVED.s).replace("%ip%", args[3])));
                        chatFilter.byPassDNS.remove(args[3].toLowerCase());
                        list.remove(args[3].toLowerCase());
                        chatFilter.getWhitelistConfig().set("bypassIP", list);
                        chatFilter.rebuildBypassReplacements();
                        chatFilter.saveWhitelist();
                        return true;
                    }
                }
                break;
            default:
                sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ARGS.s)));
        }
        return false;
    }
}
