package a4.papers.chatfilter.chatfilter;

import a4.papers.chatfilter.chatfilter.events.*;
import a4.papers.chatfilter.chatfilter.lang.Types;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.Callable;

public final class ChatFilter extends JavaPlugin {

    private static Locale SpanishLocale = new Locale("es");
    private static Locale PolishLocale = new Locale("pl");


    public ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
    public ChatFilter chatFilter;
    public ChatFilters chatFilters;
    public CommandHandler commandHandler;

    public List<String> regExWords;
    public List<String> regExDNS;
    public List<String> byPassWords;
    public List<String> byPassDNS;

    public Map convertedStrings = new HashMap();

    public HashMap<String, String> langStrings = new HashMap<String, String>();

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

    public String eventPriorityChat;
    public String eventPrioritySign;
    public String eventPriorityCommand;
    public String eventPriorityBooks;
    public String eventPriorityAnvil;



    Integer blockedInt = 1;
    int pluginId = 13946;
    Locale locale;

    public void onEnable() {
        try {
            loadLang();
        } catch (MalformedURLException e) {
        }
        chatFilters = new ChatFilters(this);
        commandHandler = new CommandHandler(this);
        Metrics metrics = new Metrics(this, pluginId);
        loadVariables();
        getCommand("chatfilter").setExecutor(new Commands(this));
        getCommand("clearchat").setExecutor(new Commands(this));
        getCommand("chatfilter").setTabCompleter(new TabComplete());
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SwearChatListener(this), this);
        pm.registerEvents(new CapsChatListener(this), this);
        pm.registerEvents(new BooksListener(this), this);
        pm.registerEvents(new SignListener(this), this);
        pm.registerEvents(new AnvilListener(this), this);
        pm.registerEvents(new ChatDelayListener(this), this);
        pm.registerEvents(new PauseChat(this), this);
        pm.registerEvents(new CommandLIstener(this), this);
        saveDefaultConfig();
        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> {
            return locale.toString();
        }));
        metrics.addCustomChart(new Metrics.SingleLineChart("block", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return blockedInt;
            }
        }));
    }

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

    public void sendStaffMessage(String str) {
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (online.hasPermission("chatfilter.view") || (online.isOp())) {
                online.sendMessage(str);
            }
        }
    }

    public void sendConsole(Types type, String msg, Player p, String regexUsed, String pl) {
        if (!type.equals(Types.NOTYPE)) {
            blockedInt = new Integer(blockedInt.intValue()+ 1);
            consoleSender.sendMessage("------- Match Type: " + type.id + " ~ " + pl.toUpperCase());
            consoleSender.sendMessage("Match: " + regexUsed);
            consoleSender.sendMessage("Catch > " + p.getName() + ": " + msg);
        }
    }

    public String stringArrayToString(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr)
            sb.append(str).append(" ");
        return sb.substring(0, sb.length() - 1);
    }

    public String replaceString(String str, CommandSender sender) {
        colour(str.replace("%player%", sender.getName()));
        return str;
    }

    public ResourceBundle fromClassLoader() throws MalformedURLException {
        String bundleName = "messages";
        File file = new File(getDataFolder().getAbsolutePath());
        URL[] urls = {file.toURI().toURL()};
        ClassLoader loader = new URLClassLoader(urls);
        return ResourceBundle.getBundle(bundleName, locale, loader);
    }

    public String mapToString(String s) {
        return convertedStrings.get(s).toString();
    }

    public void loadLang() throws MalformedURLException {
        setupLanguageFiles();
        ResourceBundle resource = fromClassLoader();
        convertedStrings = convertResourceBundleToMap(resource);
    }

    Map<String, String> convertResourceBundleToMap(ResourceBundle resource) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, resource.getString(key));
        }
        return map;
    }

    private void setupLanguageFiles() {
        String lang = getConfig().getString("locale");

        assert lang != null;
        if (lang.contains("en")) {
            locale = Locale.ENGLISH;
            logMsg("[ChatFilter] Using locale " + locale);
            File lang_enFile = new File(getDataFolder().getAbsolutePath(), "messages_en.properties");
            if (!lang_enFile.exists()) {
                this.saveResource("messages_en.properties", false);
            }
        } else if (lang.contains("zh")) {
            locale = Locale.CHINESE;
            logMsg("[ChatFilter] Using locale " + locale);
            File lang_cnFile = new File(getDataFolder().getAbsolutePath(), "messages_zh.properties");
            if (!lang_cnFile.exists()) {
                this.saveResource("messages_zh.properties", false);

            }
        } else if (lang.contains("es")) {
            locale = SpanishLocale;
            logMsg("[ChatFilter] Using locale " + locale);
            File lang_cnFile = new File(getDataFolder().getAbsolutePath(), "messages_es.properties");
            if (!lang_cnFile.exists()) {
                this.saveResource("messages_es.properties", false);
            }
        } else if (lang.contains("pl")) {
            locale = PolishLocale;
            logMsg("[ChatFilter] Using locale " + locale);
            File lang_cnFile = new File(getDataFolder().getAbsolutePath(), "messages_pl.properties");
            if (!lang_cnFile.exists()) {
                this.saveResource("messages_pl.properties", false);

            }
        } else {
            locale = Locale.ENGLISH;
            logMsg("No locale found, Using locale English default");
            File lang_enFile = new File(getDataFolder().getAbsolutePath(), "messages_en.properties");
            if (!lang_enFile.exists()) {
                this.saveResource("messages_en.properties", false);
            }
        }
    }
}
