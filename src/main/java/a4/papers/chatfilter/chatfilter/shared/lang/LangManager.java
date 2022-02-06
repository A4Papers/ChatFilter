package a4.papers.chatfilter.chatfilter.shared.lang;

import a4.papers.chatfilter.chatfilter.ChatFilter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class LangManager {

    public Locale locale;
    public Map convertedStrings = new HashMap();
    ChatFilter chatFilter;
    private Locale SpanishLocale = new Locale("es");
    private Locale PolishLocale = new Locale("pl");
    private Locale DanishLocale = new Locale("da");

    public LangManager(ChatFilter instance) {
        chatFilter = instance;
    }

    public String mapToString(String s) {
        return convertedStrings.get(s).toString();
    }

    public String stringArrayToString(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr)
            sb.append(str).append(" ");
        return sb.substring(0, sb.length() - 1);
    }

    public ResourceBundle fromClassLoader() throws MalformedURLException {
        String bundleName = "messages";
        File file = new File(chatFilter.getDataFolder().getAbsolutePath());
        URL[] urls = {file.toURI().toURL()};
        ClassLoader loader = new URLClassLoader(urls);
        return ResourceBundle.getBundle(bundleName, locale, loader);
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
        String lang = chatFilter.getConfig().getString("locale");
        assert lang != null;
        File lang_enFile = new File(chatFilter.getDataFolder().getAbsolutePath(), "messages_en.properties");
        switch (lang) {
            case "en":
                locale = Locale.ENGLISH;
                if (!lang_enFile.exists()) {
                    chatFilter.saveResource("messages_en.properties", false);
                }
                break;
            case "zh":
                locale = Locale.CHINESE;
                File lang_cnFile = new File(chatFilter.getDataFolder().getAbsolutePath(), "messages_zh.properties");
                if (!lang_cnFile.exists()) {
                    chatFilter.saveResource("messages_zh.properties", false);
                }
                break;
            case "es":
                locale = SpanishLocale;
                File lang_esFile = new File(chatFilter.getDataFolder().getAbsolutePath(), "messages_es.properties");
                if (!lang_esFile.exists()) {
                    chatFilter.saveResource("messages_es.properties", false);
                }
                break;
            case "pl":
                locale = PolishLocale;
                File lang_plFile = new File(chatFilter.getDataFolder().getAbsolutePath(), "messages_pl.properties");
                if (!lang_plFile.exists()) {
                    chatFilter.saveResource("messages_pl.properties", false);
                }
            case "da":
                locale = DanishLocale;
                File lang_daFile = new File(chatFilter.getDataFolder().getAbsolutePath(), "messages_da.properties");
                if (!lang_daFile.exists()) {
                    chatFilter.saveResource("messages_da.properties", false);
                }
                break;
            default:
                locale = Locale.ENGLISH;
                if (!lang_enFile.exists()) {
                    chatFilter.saveResource("messages_en.properties", false);
                }
        }
    }
}
