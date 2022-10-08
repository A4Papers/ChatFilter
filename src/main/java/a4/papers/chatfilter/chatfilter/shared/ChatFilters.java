package a4.papers.chatfilter.chatfilter.shared;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilters {

    ChatFilter chatFilter;

    public ChatFilters(ChatFilter instance) {
        chatFilter = instance;
    }

    private String removeBypass(String s) {
        List<String> bypassItems = new ArrayList<String>(chatFilter.byPassWords);
        bypassItems.addAll(chatFilter.byPassDNS);
        for (String removewording : bypassItems) {
            if (s.contains(removewording)) {
                s = s.replaceAll(removewording, " ");
            }
        }
        return s;
    }

    public Result validResult(String string, Player player) {
        boolean matched = false;
        boolean matchedSwear = false;
        boolean matchedIP = false;
        boolean matchedURL = false;
        String regex = "";
        Map<String, FilterWrapper> regexMap = new HashMap<>();
        String lowercaseString = removeBypass(string.toLowerCase());
        Types type = Types.NOTYPE;
        List<String> groupWords = new ArrayList<String>();
        List<String> regexUsed = new ArrayList<String>();
        if (!(player.hasPermission("chatfilter.bypass.swear"))) {
            for (Pattern p : chatFilter.wordRegexPattern) {
                Matcher m = p.matcher(lowercaseString);
                while (m.find()) {
                    if (!player.hasPermission("chatfilter.bypass.swear." + m.group(0)))
                        matched = true;
                    matchedSwear = true;
                    regex = p.pattern();
                    regexUsed.add(p.pattern());
                    if (!groupWords.contains(m.group(0))) {
                        groupWords.add(m.group(0));
                    }
                }
            }
        }

        if (!(player.hasPermission("chatfilter.bypass.ip"))) {
            for (Pattern p : chatFilter.advertRegexPattern) {
                Matcher m = p.matcher(lowercaseString);
                while (m.find()) {
                    if (!player.hasPermission("chatfilter.bypass.ip." + m.group(0)))
                        matched = true;
                    matchedIP = true;
                    regex = p.pattern();
                    if (!groupWords.contains(m.group(0))) {
                        groupWords.add(m.group(0));
                    }
                }
            }
        }

        if (!(player.hasPermission("chatfilter.bypass.url"))) {
            if (!chatFilter.settingsAllowURL) {
                Pattern p = Pattern.compile(chatFilter.URL_REGEX);
                Matcher m = p.matcher(lowercaseString);
                if (m.find()) {
                    matched = true;
                    matchedURL = true;
                    regex = chatFilter.URL_REGEX;
                }
                regexMap.put(chatFilter.URL_REGEX, new FilterWrapper("URL", Collections.singletonList("none"), chatFilter.URL_REGEX, true, false, "", false, true, false));
            }
        }

        if (matchedURL) {
            matched = true;
            type = Types.URL;
        }
        if (isFont(string)) {
            matched = true;
            type = Types.FONT;
            regex = "unicode";
            regexMap.put("unicode", new FilterWrapper("unicode", Collections.singletonList("none"), "unicode", true, false, "", true, true, true));

        }
        if (matchedSwear) {
            type = Types.SWEAR;
        }
        if (matchedIP) {
            type = Types.IP_DNS;
        }
        if (matchedSwear && matchedIP) {
            type = Types.IP_SWEAR;
        }

        String[] array = new String[groupWords.size()];
        groupWords.toArray(array);
        regexMap.putAll(chatFilter.regexWords);
        regexMap.putAll(chatFilter.regexAdvert);
        return new Result(matched, array, type, regexMap.get(regex), regexUsed);
    }

    public boolean isFont(String string) {
        boolean matchedFont = false;
        for (String s : chatFilter.unicodeWhitelist) {
            if (string.contains(s)) {
                string = string.replace(s, "");
            }
        }
        if (chatFilter.settingsBlockFancyChat) {
            for (String s : chatFilter.unicodeBlacklist.keySet()) {
                int UrangeLow = Integer.parseInt(chatFilter.unicodeBlacklist.get(s).getStart(), 16);
                int UrangeHigh = Integer.parseInt(chatFilter.unicodeBlacklist.get(s).getEnd(), 16);
                for (int iLetter = 0; iLetter < string.length(); iLetter++) {
                    int cp = string.codePointAt(iLetter);
                    if (cp >= UrangeLow && cp <= UrangeHigh) {
                        matchedFont = true;
                    }
                }
            }
        }
        return matchedFont;
    }
}
