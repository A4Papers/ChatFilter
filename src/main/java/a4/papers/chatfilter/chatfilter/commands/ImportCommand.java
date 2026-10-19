package a4.papers.chatfilter.chatfilter.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;

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
        load(sender);
        return false;
    }

    public void load(CommandSender sender) {
        File fileObj = new File(chatFilter.getDataFolder(), "data.txt");
        if (!fileObj.exists()) {
            sender.sendMessage(ChatColor.RED + "data.txt file is not found");
            return;
        }
        String importedBy = "Imported by " + sender.getName();
        ChatFilter.runTaskAsync(() -> {
            List<String> words = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileObj))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = line.trim().toLowerCase();
                    if (!word.isEmpty() && word.length() > 1) {
                        words.add(word);
                    }
                }
            } catch (Exception e) {
                ChatFilter.runTask(() -> sender.sendMessage(ChatColor.RED + "Error reading data.txt: " + e.getMessage()));
                return;
            }
            ChatFilter.runTask(() -> {
                chatFilter.getFilters().importWordFilters(words, importedBy);
                sender.sendMessage(ChatColor.GOLD + "Successfully imported " + words.size() + " words to filter.");
            });
        });
    }
}
