package a4.papers.chatfilter.chatfilter;

import a4.papers.chatfilter.chatfilter.commands.ClearChatCommand;
import a4.papers.chatfilter.chatfilter.commands.CommandHandler;
import a4.papers.chatfilter.chatfilter.commands.CommandMain;
import a4.papers.chatfilter.chatfilter.commands.TabComplete;
import a4.papers.chatfilter.chatfilter.events.*;
import a4.papers.chatfilter.chatfilter.shared.ChatFilters;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public final class ChatFilter extends JavaPlugin {

    public ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
    public ChatFilter chatFilter;
    public ChatFilters chatFilters;
    public CommandHandler commandHandler;
    public LangManager langManager;


    public List<String> regExWords;
    public List<String> regExDNS;
    public List<String> byPassWords;
    public List<String> byPassDNS;

    public int capsAmount;
    public boolean CommandsOnSwearEnabled;
    public boolean CommandsOnAdvertisesEnabled;
    public boolean antiRepeatEnabled;
    public boolean cancelChat;
    public boolean commandsOnAdvertises;
    public boolean CommandsOnSwearAndAdvertisesEnabled;
    public boolean settingsAllowURL;
    public boolean settingsBlockFancyChat;
    public boolean CommandsOnFontEnabled;
    public boolean deCap;
    public boolean chatPause = false;
    public boolean cmdCheck;

    public String CommandsOnSwearCommand;
    public String CommandsOnAdvertisesCommand;
    public String CommandsOnSwearAndAdvertisesCommand;
    public String CommandsOnFontCommand;
    public String settingsSwearHighLight;
    public String percentage;
    public String cancelChatReplace;
    public String URL_REGEX;

    Integer blockedInt = 1;
    int pluginId = 13946;

    public void onEnable() {
        chatFilters = new ChatFilters(this);
        commandHandler = new CommandHandler(this);
        langManager = new LangManager(this);
        try {
            langManager.loadLang();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        logMsg("[ChatFilter] Loaded using locale: " + getLang().locale);
        Metrics metrics = new Metrics(this, pluginId);
        loadVariables();
        getCommand("chatfilter").setExecutor(new CommandMain(this));
        getCommand("clearchat").setExecutor(new ClearChatCommand(this));
        getCommand("chatfilter").setTabCompleter(new TabComplete());
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SwearChatListener(this), this);
        pm.registerEvents(new CapsChatListener(this), this);
        pm.registerEvents(new BooksListener(this), this);
        pm.registerEvents(new SignListener(this), this);
        pm.registerEvents(new AnvilListener(this), this);
        pm.registerEvents(new ChatDelayListener(this), this);
        pm.registerEvents(new PauseChat(this), this);
        pm.registerEvents(new CommandListener(this), this);
        saveDefaultConfig();
        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> {
            return getLang().locale.toString();
        }));
        metrics.addCustomChart(new Metrics.SingleLineChart("block", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return blockedInt;
            }
        }));
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
    }

    public void loadVariables() {
        this.regExWords = getConfig().getStringList("filteredWords");
        this.regExDNS = getConfig().getStringList("filteredIPandDNS");
        this.byPassWords = getConfig().getStringList("bypassWords");
        this.byPassDNS = getConfig().getStringList("bypassIP");

        this.CommandsOnFontEnabled = getConfig().getBoolean("CommandsOnFont.enabled");
        this.CommandsOnFontCommand = getConfig().getString("CommandsOnFont.command");

        this.CommandsOnSwearEnabled = getConfig().getBoolean("CommandsOnSwear.enabled");
        this.CommandsOnSwearCommand = getConfig().getString("CommandsOnSwear.command");

        this.CommandsOnAdvertisesEnabled = getConfig().getBoolean("CommandsOnAdvertises.enabled");
        this.CommandsOnAdvertisesCommand = getConfig().getString("CommandsOnAdvertises.command");

        this.CommandsOnSwearAndAdvertisesEnabled = getConfig().getBoolean("CommandsOnSwearAndAdvertises.enabled");
        this.CommandsOnSwearAndAdvertisesCommand = getConfig().getString("CommandsOnSwearAndAdvertises.command");

        this.settingsAllowURL = getConfig().getBoolean("settings.allowURL");

        this.settingsBlockFancyChat = getConfig().getBoolean("settings.blockFancyChat");
        this.settingsSwearHighLight = getConfig().getString("settings.swearHighLight");

        this.cmdCheck = getConfig().getBoolean("checkCommands");
        this.capsAmount = getConfig().getInt("settings.capsAmount");
        this.deCap = getConfig().getBoolean("settings.deCap");
        this.percentage = getConfig().getString("settings.similarMessagePercent");
        this.antiRepeatEnabled = getConfig().getBoolean("settings.antiRepeatEnabled");
        this.cancelChat = getConfig().getBoolean("settings.cancelChat");
        this.cancelChatReplace = getConfig().getString("settings.cancelChatReplace");
        this.URL_REGEX = getConfig().getString("URL_REGEX");
    }

    public String colour(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void logMsg(String message) {
        consoleSender.sendMessage(message);
    }

    public ChatFilters getChatFilters() {
        return chatFilters;
    }

    public LangManager getLang() { return langManager; }

    public void sendStaffMessage(String str) {
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (online.hasPermission("chatfilter.view") || (online.isOp())) {
                online.sendMessage(str);
            }
        }
    }
    public String replaceString(String str, CommandSender sender) {
        colour(str.replace("%player%", sender.getName()));
        return str;
    }

    public void sendConsole(Types type, String msg, Player p, String regexUsed, String pl) {
        if (!type.equals(Types.NOTYPE)) {
            blockedInt = new Integer(blockedInt.intValue()+ 1);
            consoleSender.sendMessage("------- Match Type: " + type.id + " ~ " + pl.toUpperCase());
            consoleSender.sendMessage("Match: " + regexUsed);
            consoleSender.sendMessage("Catch > " + p.getName() + ": " + msg);
        }
    }
}
