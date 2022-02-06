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

import java.util.Collections;
import java.util.List;

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
                    Collections.sort(chatFilter.byPassWords);
                    ComponentBuilder message = new ComponentBuilder("");
                    for (String words : chatFilter.byPassWords) {
                        message.append(ChatColor.WHITE + " " + words + ", ");
                        message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cf whitelist remove word " + words));
                        message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_WORD_1.s).replace("%word%", words)))));
                    }
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_WORD_2.s)));
                    sender.spigot().sendMessage(message.create());
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_WORD_3.s)));
                    return true;
                }
                if (args[2].equals("ip")) {
                    Collections.sort(chatFilter.byPassDNS);
                    ComponentBuilder message = new ComponentBuilder("");
                    for (String words : chatFilter.byPassDNS) {
                        message.append(ChatColor.WHITE + " " + words + ", ");
                        message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cf whitelist remove ip " + words));
                        message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_WORD_1.s).replace("%ip%", words)))));
                    }
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_IP_2.s)));
                    sender.spigot().sendMessage(message.create());
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_IP_3.s)));
                    return true;
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
                    String ArgsString = String.join(" ", args).toLowerCase().replaceAll("whitelist add ip ", "");
                    List<String> list = chatFilter.getWhitelistConfig().getStringList("bypassIP");
                    if (list.contains(ArgsString)) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_IP_NO.s).replace("%ip%", ArgsString)));
                        return true;
                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_IP_ADDED.s).replace("%ip%", ArgsString)));
                        chatFilter.byPassDNS.add(ArgsString);
                        list.add(ArgsString);
                        chatFilter.getWhitelistConfig().set("bypassIP", list);
                        chatFilter.save();
                        return true;
                    }
                }
                if (args[2].equals("word") && args.length > 3) {
                    String ArgsString = String.join(" ", args).toLowerCase().replaceAll("whitelist add word ", "");
                    List<String> list = chatFilter.getWhitelistConfig().getStringList("bypassWords");
                    if (list.contains(ArgsString)) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_WORD_NO.s).replace("%word%", ArgsString)));
                        return true;

                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_ADD_WORD_ADDED.s).replace("%word%", ArgsString)));
                        chatFilter.byPassWords.add(ArgsString);
                        list.add(ArgsString);
                        chatFilter.getWhitelistConfig().set("bypassWords", list);
                        chatFilter.save();
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
                    String ArgsString = String.join(" ", args).toLowerCase().replaceAll("whitelist remove word ", "");
                    List<String> list = chatFilter.getWhitelistConfig().getStringList("bypassWords");
                    if (!list.contains(ArgsString)) {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE_WORD_NO.s).replace("%word%", ArgsString)));
                    } else {
                        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE_WORD_REMOVED.s).replace("%word%", ArgsString)));
                        chatFilter.byPassWords.remove(ArgsString);
                        list.remove(ArgsString);
                        chatFilter.getWhitelistConfig().set("bypassWords", list);
                        chatFilter.save();
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
                        chatFilter.save();
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
