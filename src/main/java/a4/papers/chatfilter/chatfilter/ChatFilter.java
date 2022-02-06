package a4.papers.chatfilter.chatfilter;

import a4.papers.chatfilter.chatfilter.commands.ClearChatCommand;
import a4.papers.chatfilter.chatfilter.commands.CommandHandler;
import a4.papers.chatfilter.chatfilter.commands.CommandMain;
import a4.papers.chatfilter.chatfilter.commands.TabComplete;
import a4.papers.chatfilter.chatfilter.events.*;
import a4.papers.chatfilter.chatfilter.shared.ChatFilters;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.LangManager;
import a4.papers.chatfilter.chatfilter.shared.regexHandler.LoadFilters;
import a4.papers.chatfilter.chatfilter.shared.regexHandler.RegexpGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public final class ChatFilter extends JavaPlugin {

    public ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
    public ChatFilter chatFilter;
    public ChatFilters chatFilters;
    public CommandHandler commandHandler;
    public LangManager langManager;
    public LoadFilters loadFilters;
    public FilterWrapper filterWrapper;
    public RegexpGenerator regexpGenerator;
    public Manager manager;
    public Map<String, FilterWrapper> regexWords;
    public Map<String, FilterWrapper> regexAdvert;
    public List<String> byPassWords;
    public List<String> byPassDNS;
    public int capsAmount;
    public boolean CommandsOnSwearEnabled;
    public boolean CommandsOnAdvertisesEnabled;
    public boolean antiRepeatEnabled;
    public boolean cancelChat;
    public boolean CommandsOnSwearAndAdvertisesEnabled;
    public boolean settingsAllowURL;
    public boolean settingsBlockFancyChat;
    public boolean CommandsOnFontEnabled;
    public boolean deCap;
    public boolean chatPause = false;
    public boolean cmdCheck;
    public boolean enableLeetSpeak;
    public String CommandsOnSwearCommand;
    public String CommandsOnAdvertisesCommand;
    public String CommandsOnSwearAndAdvertisesCommand;
    public String CommandsOnFontCommand;
    public String settingsSwearHighLight;
    public String percentage;
    public String cancelChatReplace;
    public String URL_REGEX;
    private Integer blockedInt = 1;
    private File wordConfigFile;
    private File advertConfigFile;
    private File whitelistConfigFile;
    private FileConfiguration whitelistConfig;
    private FileConfiguration wordConfig;
    private FileConfiguration advertConfig;


    public void onEnable() {
        regexWords = new HashMap<>();
        regexAdvert = new HashMap<>();
        chatFilters = new ChatFilters(this);
        commandHandler = new CommandHandler(this);
        langManager = new LangManager(this);
        loadFilters = new LoadFilters(this);
        regexpGenerator = new RegexpGenerator(this);
        manager = new Manager(this);

        try {
            langManager.loadLang();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        createCustomConfig();
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
        getFilters().loadWordFilter();
        getFilters().loadAdvertFilter();
        logMsg("[ChatFilter] Loaded using locale: " + getLang().locale);
        logMsg("[ChatFilter] " + regexWords.size() + " Enabled word filters.");
        logMsg("[ChatFilter] " + regexAdvert.size() + " Enabled advertising filters.");
        logMsg("[ChatFilter] " + (byPassWords.size() + byPassWords.size()) + " whitelisted items.");
        int pluginId = 13946;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> {
            return getLang().locale.toString();
        }));
        metrics.addCustomChart(new Metrics.SimplePie("total_filters", () -> {
            return String.valueOf(regexWords.size() + regexAdvert.size());
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
        this.byPassWords = getWhitelistConfig().getStringList("bypassWords");
        this.byPassDNS = getWhitelistConfig().getStringList("bypassIP");
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
        this.enableLeetSpeak = getConfig().getBoolean("enableLeetSpeak");
    }

    public String colour(String s) {
        if (manager.supported("hex")) {
            return manager.colorStringHex(s);
        } else {
            return ChatColor.translateAlternateColorCodes('&', s);
        }
    }

    public void logMsg(String message) {
        consoleSender.sendMessage(message);
    }

    public LoadFilters getFilters() {
        return loadFilters;
    }

    public ChatFilters getChatFilters() {
        return chatFilters;
    }

    public LangManager getLang() {
        return langManager;
    }

    public FileConfiguration getWordConfig() {
        return this.wordConfig;
    }

    public FileConfiguration getAdvertConfig() {
        return this.advertConfig;
    }

    public FileConfiguration getWhitelistConfig() {
        return this.whitelistConfig;
    }

    public RegexpGenerator regexpGenerator() {
        return this.regexpGenerator;
    }

    public void sendStaffMessage(String str) {
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (online.hasPermission("chatfilter.view") || (online.isOp())) {
                online.sendMessage(str);
            }
        }
    }

    public void sendConsole(Types type, String msg, Player p, String regexUsed, String pl) {
        if (!type.equals(Types.NOTYPE)) {
            blockedInt = new Integer(blockedInt.intValue() + 1);
            consoleSender.sendMessage("------- Match Type: " + type.id + " ~ " + pl.toUpperCase());
            consoleSender.sendMessage("Match: " + regexUsed);
            consoleSender.sendMessage("Catch > " + p.getName() + ": " + msg);
        }
    }

    private void createCustomConfig() {
        wordConfigFile = new File(getDataFolder(), "wordFilters.yml");
        advertConfigFile = new File(getDataFolder(), "advertFilters.yml");
        whitelistConfigFile = new File(getDataFolder(), "whitelisted.yml");
        if (!whitelistConfigFile.exists()) {
            whitelistConfigFile.getParentFile().mkdirs();
            saveResource("whitelisted.yml", false);
        }
        if (!wordConfigFile.exists()) {
            wordConfigFile.getParentFile().mkdirs();
            saveResource("wordFilters.yml", false);
        }
        if (!advertConfigFile.exists()) {
            advertConfigFile.getParentFile().mkdirs();
            saveResource("advertFilters.yml", false);
        }
        whitelistConfig = new YamlConfiguration();
        wordConfig = new YamlConfiguration();
        advertConfig = new YamlConfiguration();
        try {
            whitelistConfig.load(whitelistConfigFile);
            advertConfig.load(advertConfigFile);
            wordConfig.load(wordConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            whitelistConfig.save(whitelistConfigFile);
            advertConfig.save(advertConfigFile);
            wordConfig.save(wordConfigFile);
        } catch (IOException ignored) {
        }
    }

    public void reloadConfigs() throws Exception {
        whitelistConfig.load(whitelistConfigFile);
        advertConfig.load(advertConfigFile);
        wordConfig.load(wordConfigFile);
    }
}
