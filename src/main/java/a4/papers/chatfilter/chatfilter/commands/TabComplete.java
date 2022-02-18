package a4.papers.chatfilter.chatfilter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {
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
            List<String> arguments2 = new ArrayList<>();
            arguments2.add("add");
            arguments2.add("list");
            arguments2.add("remove");
            return arguments2;
        } else if (args.length == 3) {
            List<String> arguments3 = new ArrayList<>();
            arguments3.add("word");
            arguments3.add("ip");
            return arguments3;
        }
        return Collections.emptyList();
    }
}
