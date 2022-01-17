package a4.papers.chatfilter.chatfilter;

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
            arguments1.add("whitelist");
            arguments1.add("blacklist");
            arguments1.add("reload");
            arguments1.add("pause");
            arguments1.add("clear");
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
