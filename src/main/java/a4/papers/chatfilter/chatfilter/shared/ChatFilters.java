package a4.papers.chatfilter.chatfilter.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import a4.papers.chatfilter.chatfilter.ChatFilter;

public class ChatFilters {

    private static final FilterWrapper URL_FILTER = new FilterWrapper("URL", Collections.singletonList("none"), "URL", true, false, "", false, true, false);
    private static final FilterWrapper FONT_FILTER = new FilterWrapper("unicode", Collections.singletonList("none"), "unicode", true, false, "", true, true, true);
    private static final String[] EMPTY_MATCHES = new String[0];
    private static final List<String> EMPTY_REGEX_LIST = Collections.emptyList();
    private static final Result NOTYPE_RESULT = new Result(false, EMPTY_MATCHES, Types.NOTYPE, null, EMPTY_REGEX_LIST);

    ChatFilter chatFilter;

    public ChatFilters(ChatFilter instance) {
        chatFilter = instance;
    }

    private String removeBypass(String s) {
        List<String> byPassWords = chatFilter.byPassWords;
        if (!byPassWords.isEmpty()) {
            for (int i = 0, size = byPassWords.size(); i < size; i++) {
                String removewording = byPassWords.get(i);
                if (s.contains(removewording)) {
                    s = s.replace(removewording, " ");
                }
            }
        }
        List<String> byPassDNS = chatFilter.byPassDNS;
        if (!byPassDNS.isEmpty()) {
            for (int i = 0, size = byPassDNS.size(); i < size; i++) {
                String removewording = byPassDNS.get(i);
                if (s.contains(removewording)) {
                    s = s.replace(removewording, " ");
                }
            }
        }
        return s;
    }

    public Result validResult(String string, Player player) {
        CompiledFilter[] wordCompiled = chatFilter.wordCompiled;
        CompiledFilter[] advertCompiled = chatFilter.advertCompiled;
        boolean isOp = player.isOp();
        boolean canBypassSwear = isOp || player.hasPermission("chatfilter.bypass.swear");
        boolean canBypassIP = isOp || player.hasPermission("chatfilter.bypass.ip");
        boolean canBypassURL = isOp || player.hasPermission("chatfilter.bypass.url");
        boolean fontCheckNeeded = chatFilter.settingsBlockFancyChat && !chatFilter.unicodeBlacklist.isEmpty();

        if ((canBypassSwear || wordCompiled.length == 0)
                && (canBypassIP || advertCompiled.length == 0)
                && (canBypassURL || chatFilter.settingsAllowURL)
                && !fontCheckNeeded) {
            return NOTYPE_RESULT;
        }

        String lowercaseString = removeBypass(string.toLowerCase());

        boolean matched = false;
        boolean matchedSwear = false;
        boolean matchedIP = false;
        boolean matchedURL = false;
        String regex = null;
        FilterWrapper matchedWrapper = null;

        Set<String> groupWords = new LinkedHashSet<>();
        List<String> regexUsed = new ArrayList<>();

        if (!canBypassSwear && wordCompiled.length > 0) {
            for (int i = 0; i < wordCompiled.length; i++) {
                CompiledFilter compiled = wordCompiled[i];
                Matcher m = compiled.getPattern().matcher(lowercaseString);
                while (m.find()) {
                    String match = m.group(0);
                    if (!player.hasPermission("chatfilter.bypass.swear." + match)) {
                        matched = true;
                        matchedSwear = true;
                        regex = compiled.getPattern().pattern();
                        matchedWrapper = compiled.getWrapper();
                        regexUsed.add(regex);
                        groupWords.add(match);
                    }
                }
            }
        }

        if (!canBypassIP && advertCompiled.length > 0) {
            for (int i = 0; i < advertCompiled.length; i++) {
                CompiledFilter compiled = advertCompiled[i];
                Matcher m = compiled.getPattern().matcher(lowercaseString);
                while (m.find()) {
                    String match = m.group(0);
                    if (!player.hasPermission("chatfilter.bypass.ip." + match)) {
                        matched = true;
                        matchedIP = true;
                        regex = compiled.getPattern().pattern();
                        matchedWrapper = compiled.getWrapper();
                        groupWords.add(match);
                    }
                }
            }
        }

        if (!canBypassURL && !chatFilter.settingsAllowURL) {
            Matcher m = chatFilter.urlPattern.matcher(lowercaseString);
            if (m.find()) {
                matched = true;
                matchedURL = true;
                regex = chatFilter.URL_REGEX;
                if (matchedWrapper == null) {
                    matchedWrapper = URL_FILTER;
                }
            }
        }

        if (fontCheckNeeded && isFont(string)) {
            matched = true;
            regex = "unicode";
            if (matchedWrapper == null) {
                matchedWrapper = FONT_FILTER;
            }
        }

        Types type;
        if (matchedSwear && matchedIP) {
            type = Types.IP_SWEAR;
        } else if (matchedSwear) {
            type = Types.SWEAR;
        } else if (matchedIP) {
            type = Types.IP_DNS;
        } else if (matchedURL) {
            type = Types.URL;
        } else if (matched) {
            type = Types.FONT;
        } else {
            type = Types.NOTYPE;
        }

        String[] array = groupWords.toArray(EMPTY_MATCHES);
        if (matchedWrapper == null && regex != null) {
            matchedWrapper = chatFilter.regexWords.get(regex);
            if (matchedWrapper == null) {
                matchedWrapper = chatFilter.regexAdvert.get(regex);
            }
        }
        return new Result(matched, array, type, matchedWrapper, regexUsed);
    }

    public boolean isFont(String string) {
        if (!chatFilter.settingsBlockFancyChat || chatFilter.unicodeBlacklist.isEmpty()) {
            return false;
        }

        String processedString = string;
        List<String> unicodeWhitelist = chatFilter.unicodeWhitelist;
        if (!unicodeWhitelist.isEmpty()) {
            for (int i = 0, size = unicodeWhitelist.size(); i < size; i++) {
                String s = unicodeWhitelist.get(i);
                if (processedString.indexOf(s) >= 0) {
                    processedString = processedString.replace(s, "");
                }
            }
        }

        if (processedString.isEmpty()) {
            return false;
        }

        int[] ranges = chatFilter.unicodeRanges;
        if (ranges.length == 0) {
            return false;
        }
        int rangeCount = ranges.length >> 1;
        int length = processedString.length();
        for (int i = 0; i < length; ) {
            int cp = processedString.codePointAt(i);
            for (int r = 0; r < rangeCount; r++) {
                if (cp >= ranges[r << 1] && cp <= ranges[(r << 1) + 1]) {
                    return true;
                }
            }
            i += Character.charCount(cp);
        }
        return false;
    }

    public boolean containsNonEnglishLetters(String string) {
        if (!chatFilter.settingsBlockCustomSybols) {
            return false;
        }
        if (string == null || string.isEmpty()) {
            return false;
        }
        int length = string.length();
        for (int i = 0; i < length; ) {
            int cp = string.codePointAt(i);
            if (Character.isLetter(cp)) {
                if (!((cp >= 'A' && cp <= 'Z') || (cp >= 'a' && cp <= 'z'))) {
                    return true;
                }
            }
            i += Character.charCount(cp);
        }
        return false;
    }
}
