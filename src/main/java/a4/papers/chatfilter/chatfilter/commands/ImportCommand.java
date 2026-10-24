package a4.papers.chatfilter.chatfilter.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.scheduling.PluginScheduler;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;

public class ImportCommand implements CommandExecutor {

    ChatFilter chatFilter;
    private final AtomicBoolean importing = new AtomicBoolean();

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
        if (!importing.compareAndSet(false, true)) {
            sender.sendMessage(ChatColor.RED + "An import is already running.");
            return;
        }
        File fileObj = new File(chatFilter.getDataFolder(), "data.txt");
        String importedBy = "Imported by " + sender.getName();
        PluginScheduler tasks = chatFilter.tasks();
        enqueue(() -> tasks.async(() -> read(tasks, sender, fileObj, importedBy)));
    }

    private void read(PluginScheduler tasks, CommandSender sender, File file, String importedBy) {
        List<String> words = new ArrayList<>();
        try {
            if (!file.exists()) {
                importing.set(false);
                tasks.sender(sender, () -> sender.sendMessage(ChatColor.RED + "data.txt file is not found"));
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = line.trim().toLowerCase();
                    if (word.length() > 1) {
                        words.add(word);
                    }
                }
            }
        } catch (Exception error) {
            importing.set(false);
            tasks.sender(sender, () -> sender.sendMessage(ChatColor.RED + "Error reading data.txt: " + error.getMessage()));
            return;
        }
        enqueue(() -> tasks.global(() -> apply(tasks, sender, words, importedBy)));
    }

    private void apply(PluginScheduler tasks, CommandSender sender, List<String> words, String importedBy) {
        try {
            chatFilter.getFilters().importWordFilters(words, importedBy);
            tasks.sender(sender, () -> sender.sendMessage(ChatColor.GOLD + "Successfully imported " + words.size() + " words to filter."));
        } catch (Exception error) {
            tasks.sender(sender, () -> sender.sendMessage(ChatColor.RED + "Error importing data.txt: " + error.getMessage()));
        } finally {
            importing.set(false);
        }
    }

    private void enqueue(Supplier<ScheduledTask> registration) {
        boolean queued = false;
        try {
            queued = registration.get() != null;
        } finally {
            if (!queued) {
                importing.set(false);
            }
        }
    }
}
