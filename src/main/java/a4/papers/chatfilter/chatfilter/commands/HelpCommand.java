package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    ChatFilter chatFilter;

    public HelpCommand(ChatFilter instance) {
        chatFilter = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.HELP_VERSION.s).replace("%version%", chatFilter.getDescription().getVersion())));
        sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.HOVER_OVER_COMMAND_TITLE.s)));
        TextComponent clearchat = new TextComponent(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_CLEAR.s)));
        clearchat.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_CLEAR_HOVER.s))).create()));

        TextComponent messageblacklistw = new TextComponent(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_WORD.s)));
        messageblacklistw.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_WORD_HOVER.s))).create()));
        messageblacklistw.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/chatfilter blacklist add word "));

        TextComponent messageblacklistip = new TextComponent(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_IP.s)));
        messageblacklistip.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_IP_HOVER.s))).create()));
        messageblacklistip.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/chatfilter blacklist add ip "));

        TextComponent messageblacklistlist = new TextComponent(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_LIST.s)));
        messageblacklistlist.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_IP_HOVER.s))).create()));
        messageblacklistlist.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/chatfilter blacklist list "));

        TextComponent messagewhitelistword = new TextComponent(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_WORD.s)));
        messagewhitelistword.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_WORD_HOVER.s))).create()));
        messagewhitelistword.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/chatfilter whitelist add word "));

        TextComponent messagewhitelistip = new TextComponent(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_IP.s)));
        messagewhitelistip.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_IP_HOVER.s))).create()));
        messagewhitelistip.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/chatfilter whitelist add ip "));

        TextComponent messagewhitelistlist = new TextComponent(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST.s)));
        messagewhitelistlist.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_LIST_HOVER.s))).create()));
        messagewhitelistlist.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/chatfilter whitelist list "));

        TextComponent messageblacklistremove = new TextComponent(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE.s)));
        messageblacklistremove.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_BLACKLIST_REMOVE_HOVER.s))).create()));
        messageblacklistremove.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/chatfilter blacklist remove "));

        TextComponent messagewhitelistremove = new TextComponent(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE.s)));
        messagewhitelistremove.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_WHITELIST_REMOVE_HOVER.s))).create()));
        messagewhitelistremove.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/chatfilter whitelist remove "));

        TextComponent messagepause = new TextComponent(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_PAUSE.s)));
        messagepause.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.CMD_PAUSE_HOVER.s))).create()));
        messagepause.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/chatfilter pause"));

        sender.spigot().sendMessage(clearchat);
        sender.spigot().sendMessage(messagepause);
        sender.spigot().sendMessage(messageblacklistw);
        sender.spigot().sendMessage(messageblacklistip);
        sender.spigot().sendMessage(messagewhitelistword);
        sender.spigot().sendMessage(messagewhitelistip);
        sender.spigot().sendMessage(messageblacklistlist);
        sender.spigot().sendMessage(messagewhitelistlist);
        sender.spigot().sendMessage(messageblacklistremove);
        sender.spigot().sendMessage(messagewhitelistremove);
        return true;
    }
}
