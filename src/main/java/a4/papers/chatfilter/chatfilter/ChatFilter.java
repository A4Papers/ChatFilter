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
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class ChatFilter extends JavaPlugin {

    public ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
    public ChatFilter chatFilter;
    public ChatFilters chatFilters;
    public CommandHandler commandHandler;
    public LangManager langManager;
    public LoadFilters loadFilters;
    public FilterWrapper filterWrapper;
    public RegexpGenerator regexpGenerator;
    public Manager manager;
    public List<Pattern> wordRegexPattern = new ArrayList<>();
    public List<Pattern> advertRegexPattern = new ArrayList<>();
    public Map<String, FilterWrapper> regexWords;
    public Map<String, FilterWrapper> regexAdvert;
    public List<String> byPassWords;
    public List<String> byPassDNS;
    public List<String> defaultWordAction;
    public List<String> defaultIPAction;
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
    public boolean antiSpamEnabled;
    public boolean defaultIPEnabled;
    public boolean defaultIPWarnStaff;
    public boolean defaultIPWarnPlayer;
    public boolean defaultIPWarnConsole;
    public boolean defaultIPCancelChatCancel;
    public boolean defaultIPCancelReplace;
    public boolean defaultWordEnabled;
    public boolean defaultWordWarnStaff;
    public boolean defaultWordWarnPlayer;
    public boolean defaultWordWarnConsole;
    public boolean defaultWordCancelChatCancel;
    public boolean defaultWordCancelReplace;
    public int antiSpamAboveAmount;
    public int antiSpamReplaceAmount;
    public String defaultWordCancelReplaceWith;
    public String defaultIPCancelReplaceWith;
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
        SwearChatListener scl = new SwearChatListener(this);
        CapsChatListener ccl = new CapsChatListener(this);
        BooksListener bl = new BooksListener(this);
        SignListener sl = new SignListener(this);
        AnvilListener al = new AnvilListener(this);
        ChatDelayListener cdl = new ChatDelayListener(this);
        PauseChat pc = new PauseChat(this);
        CommandListener cl = new CommandListener(this);
        RepeatCharListener rcl = new RepeatCharListener(this);
        pm.registerEvent(AsyncPlayerChatEvent.class, scl, EventPriority.valueOf(getConfig().getString("EventPriority.SwearListener")), scl, this, true);
        pm.registerEvent(AsyncPlayerChatEvent.class, ccl, EventPriority.valueOf(getConfig().getString("EventPriority.CapsListener")), ccl, this, true);
        pm.registerEvent(PlayerEditBookEvent.class, bl, EventPriority.valueOf(getConfig().getString("EventPriority.BookListener")), bl, this, true);
        pm.registerEvent(SignChangeEvent.class, sl, EventPriority.valueOf(getConfig().getString("EventPriority.SignListener")), sl, this, true);
        pm.registerEvent(InventoryClickEvent.class, al, EventPriority.valueOf(getConfig().getString("EventPriority.AdvilListener")), al, this, true);
        pm.registerEvent(AsyncPlayerChatEvent.class, cdl, EventPriority.valueOf(getConfig().getString("EventPriority.ChatDelayListener")), cdl, this, true);
        pm.registerEvent(AsyncPlayerChatEvent.class, pc, EventPriority.valueOf(getConfig().getString("EventPriority.PauseChatListener")), pc, this, true);
        pm.registerEvent(PlayerCommandPreprocessEvent.class, cl, EventPriority.valueOf(getConfig().getString("EventPriority.CommandListener")), cl, this, true);
        pm.registerEvent(AsyncPlayerChatEvent.class, rcl, EventPriority.valueOf(getConfig().getString("EventPriority.RepeatCharListener")), rcl, this, true);
        saveDefaultConfig();
        getFilters().loadWordFilter();
        getFilters().loadAdvertFilter();
        getFilters().regexCompile();
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
        this.antiSpamEnabled = getConfig().getBoolean("antiSpam.enabled");
        this.antiSpamAboveAmount = getConfig().getInt("antiSpam.aboveAmount");
        this.antiSpamReplaceAmount = getConfig().getInt("antiSpam.replaceAmount");

        this.defaultWordEnabled = getConfig().getBoolean("default.word.Enabled");
        this.defaultWordWarnStaff = getConfig().getBoolean("default.word.Warn.Staff");
        this.defaultWordWarnPlayer = getConfig().getBoolean("default.word.Warn.Player");
        this.defaultWordWarnConsole = getConfig().getBoolean("default.word.Warn.Console");
        this.defaultWordCancelChatCancel = getConfig().getBoolean("default.word.CancelChat.Cancel");
        this.defaultWordCancelReplace = getConfig().getBoolean("default.word.CancelChat.Replace");
        this.defaultWordCancelReplaceWith = getConfig().getString("default.word.CancelChat.ReplaceWith");
        this.defaultWordAction = getConfig().getStringList("default.word.Action");
        this.defaultIPEnabled = getConfig().getBoolean("default.ip.Enabled");
        this.defaultIPWarnStaff = getConfig().getBoolean("default.ip.Warn.Staff");
        this.defaultIPWarnPlayer = getConfig().getBoolean("default.ip.Warn.Player");
        this.defaultIPWarnConsole = getConfig().getBoolean("default.ip.Warn.Console");
        this.defaultIPCancelChatCancel = getConfig().getBoolean("default.ip.CancelChat.Cancel");
        this.defaultIPCancelReplace = getConfig().getBoolean("default.ip.CancelChat.Replace");
        this.defaultIPCancelReplaceWith = getConfig().getString("default.ip.CancelChat.ReplaceWith");
        this.defaultIPAction = getConfig().getStringList("default.ip.Action");
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
