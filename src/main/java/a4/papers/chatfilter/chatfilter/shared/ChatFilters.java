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
    private static final Result FONT_RESULT = new Result(true, EMPTY_MATCHES, Types.FONT, FONT_FILTER, EMPTY_REGEX_LIST);

    ChatFilter chatFilter;

    public ChatFilters(ChatFilter instance) {
        chatFilter = instance;
    }

    private String removeBypass(String s) {
        return chatFilter.bypassReplacements.replace(s, " ");
    }

    public Result validResult(String string, Player player) {
        FilterSnapshot snapshot = chatFilter.compiledFilters;
        CompiledFilter[] wordCompiled = snapshot.words();
        CompiledFilter[] advertCompiled = snapshot.adverts();
        boolean isOp = player.isOp();
        boolean checkWords = wordCompiled.length != 0 && !isOp && !player.hasPermission("chatfilter.bypass.swear");
        boolean checkAdverts = advertCompiled.length != 0 && !isOp && !player.hasPermission("chatfilter.bypass.ip");
        boolean checkURL = !chatFilter.settingsAllowURL && !isOp && !player.hasPermission("chatfilter.bypass.url");
        boolean fontCheckNeeded = chatFilter.settingsBlockFancyChat && !chatFilter.unicodeIndex.isEmpty();

        if (!checkWords && !checkAdverts && !checkURL) {
            return fontCheckNeeded && isFont(string) ? FONT_RESULT : NOTYPE_RESULT;
        }

        String lowercaseString = removeBypass(string.toLowerCase());
        long lowCharacters = 0;
        long highCharacters = 0;
        if (checkWords || checkAdverts) {
            for (int i = 0; i < lowercaseString.length(); i++) {
                char c = lowercaseString.charAt(i);
                if (c < 64) lowCharacters |= 1L << c;
                else if (c < 128) highCharacters |= 1L << (c - 64);
            }
        }

        boolean matched = false;
        boolean matchedSwear = false;
        boolean matchedIP = false;
        boolean matchedURL = false;
        String regex = null;
        FilterWrapper matchedWrapper = null;

        Set<String> groupWords = null;
        List<String> regexUsed = null;

        if (checkWords) {
            for (int i = 0; i < wordCompiled.length; i++) {
                CompiledFilter compiled = wordCompiled[i];
                if (!compiled.mightMatch(lowCharacters, highCharacters)) {
                    continue;
                }
                Matcher m = compiled.getPattern().matcher(lowercaseString);
                while (m.find()) {
                    String match = m.group(0);
                    if (!player.hasPermission("chatfilter.bypass.swear." + match)) {
                        matched = true;
                        matchedSwear = true;
                        regex = compiled.getPattern().pattern();
                        matchedWrapper = compiled.getWrapper();
                        if (regexUsed == null) {
                            regexUsed = new ArrayList<>();
                        }
                        if (groupWords == null) {
                            groupWords = new LinkedHashSet<>();
                        }
                        regexUsed.add(regex);
                        groupWords.add(match);
                    }
                }
            }
        }

        if (checkAdverts) {
            for (int i = 0; i < advertCompiled.length; i++) {
                CompiledFilter compiled = advertCompiled[i];
                if (!compiled.mightMatch(lowCharacters, highCharacters)) {
                    continue;
                }
                Matcher m = compiled.getPattern().matcher(lowercaseString);
                while (m.find()) {
                    String match = m.group(0);
                    if (!player.hasPermission("chatfilter.bypass.ip." + match)) {
                        matched = true;
                        matchedIP = true;
                        regex = compiled.getPattern().pattern();
                        matchedWrapper = compiled.getWrapper();
                        if (groupWords == null) {
                            groupWords = new LinkedHashSet<>();
                        }
                        groupWords.add(match);
                    }
                }
            }
        }

        if (checkURL) {
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

        if (!matched) {
            return NOTYPE_RESULT;
        }
        String[] array = groupWords == null ? EMPTY_MATCHES : groupWords.toArray(EMPTY_MATCHES);
        if (matchedWrapper == null && regex != null) {
            matchedWrapper = chatFilter.regexWords.get(regex);
            if (matchedWrapper == null) {
                matchedWrapper = chatFilter.regexAdvert.get(regex);
            }
        }
        return new Result(true, array, type, matchedWrapper, regexUsed == null ? EMPTY_REGEX_LIST : regexUsed);
    }

    public boolean isFont(String string) {
        CodePointIndex index = chatFilter.unicodeIndex;
        if (!chatFilter.settingsBlockFancyChat || index.isEmpty()) {
            return false;
        }

        String processedString = chatFilter.unicodeReplacements.replace(string, "");

        if (processedString.isEmpty()) {
            return false;
        }

        int length = processedString.length();
        for (int i = 0; i < length; ) {
            int cp = processedString.codePointAt(i);
            if (index.contains(cp)) {
                return true;
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
