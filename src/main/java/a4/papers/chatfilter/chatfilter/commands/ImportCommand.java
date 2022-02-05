package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ImportCommand implements CommandExecutor {

    ChatFilter chatFilter;

    public ImportCommand(ChatFilter instance) {
        chatFilter = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("chatfilter.import")) {
            sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.NO_PERMISSION.s)));
            return true;
        }
        if (sender.hasPermission("chatfilter.import")) {
            load(sender);
        }
        return false;
    }

    public void load(CommandSender sender) {
        try {
            File fileObj = new File(chatFilter.getDataFolder(), "data.txt");
            BufferedReader reader = new BufferedReader(new FileReader(fileObj));
            String l;
            while ((l = reader.readLine()) != null) {
                String word = l.replace(" ", "");
                chatFilter.getFilters().createWordFilter(word, "Imported by " + sender.getName());
                sender.sendMessage(word + ChatColor.GOLD + " Added to filter.");
            }
            reader.close();
        } catch (Throwable t) {
            sender.sendMessage(ChatColor.RED + "data.txt file is not found");
        }
    }
}
