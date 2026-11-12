package a4.papers.chatfilter.chatfilter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {
    private static final List<String> ACTIONS = List.of("add", "list", "remove");
    private static final List<String> TARGETS = List.of("word", "ip");
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> arguments1 = new ArrayList<>();
            if (sender.hasPermission("chatfilter.whitelist")) {
                arguments1.add("whitelist");
            }
            if (sender.hasPermission("chatfilter.blacklist")) {
                arguments1.add("blacklist");
            }
            if (sender.hasPermission("chatfilter.reload")) {
                arguments1.add("reload");
            }
            if (sender.hasPermission("chatfilter.pause")) {
                arguments1.add("pause");
            }
            if (sender.hasPermission("chatfilter.clear")) {
                arguments1.add("clear");
            }
            arguments1.add("help");
            return arguments1;
        } else if (args.length == 2) {
            return ACTIONS;
        } else if (args.length == 3) {
            return TARGETS;
        }
        return Collections.emptyList();
    }
}
