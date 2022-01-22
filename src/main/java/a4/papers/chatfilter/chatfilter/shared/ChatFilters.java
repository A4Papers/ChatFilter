package a4.papers.chatfilter.chatfilter.shared;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilters {

    ChatFilter chatFilter;

    public ChatFilters(ChatFilter instance) {
        chatFilter = instance;
    }

    public Result validResult(String string, Player player) {

        boolean matched = false;
        boolean matchedSwear = false;
        boolean matchedIP = false;
        boolean matchedURL = false;
        String regexUse = "";


        String lowercaseString = string.toLowerCase();
        Types type = Types.NOTYPE;
        List<String> list = new ArrayList<String>();
        List<String> bypassItems = new ArrayList<String>(chatFilter.regExWords);

        bypassItems.addAll(chatFilter.byPassDNS);

        for (String removewording : bypassItems) {
            if (lowercaseString.contains(removewording)) {
                lowercaseString = lowercaseString.replaceAll(removewording, " ");
            }
        }

        if (!(player.hasPermission("chatfilter.bypass.swear"))) {
            for (String matchSwear : chatFilter.regExWords) {
                Pattern p = Pattern.compile(matchSwear);
                Matcher m = p.matcher(lowercaseString);
                while (m.find()) {
                    matched = true;
                    matchedSwear = true;
                    regexUse = p.pattern();
                    if (!list.contains(m.group(0))) {
                        list.add(m.group(0).replace(" ",""));
                    }
                }
            }
        }

        if (!(player.hasPermission("chatfilter.bypass.ip"))) {
            for (String StringMatchedDNS : chatFilter.regExDNS) {
                Pattern p = Pattern.compile(StringMatchedDNS);
                Matcher m = p.matcher(lowercaseString);
                while (m.find()) {
                    matched = true;
                    matchedIP = true;
                    regexUse = p.pattern();
                    if (!list.contains(m.group(0))) {
                        list.add(m.group(0).replace(" ",""));
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
                }
            }
        }

        if (matchedURL) {
            matched = true;
            type = Types.URL;
        }
        if (isFont(string)) {
            matched = true;
            type = Types.FONT;
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
        String[] array = new String[list.size()];
        list.toArray(array);
        return new Result(matched, array, type, regexUse);
    }

    public boolean isFont(String string) {
        boolean matchedFont = false;
        if (chatFilter.settingsBlockFancyChat) {
            if (string.contains("\uff41") || string.contains("\uff42") || string.contains("\uff43") || string.contains("\uff44") || string.contains("\uff45") || string.contains("\uff46") || string.contains("\uff47") || string.contains("\uff48") || string.contains("\uff49") || string.contains("\uff4a") || string.contains("\uff4b") || string.contains("\uff4c") || string.contains("\uff4d") || string.contains("\uff4e") || string.contains("\uff4f") || string.contains("\uff50") || string.contains("\uff52") || string.contains("\uff53") || string.contains("\uff54") || string.contains("\uff55") || string.contains("\uff57") || string.contains("\uff58") || string.contains("\uff59") || string.contains("\uff5a") || string.contains("\uff21") || string.contains("\uff22") || string.contains("\uff23") || string.contains("\uff24") || string.contains("\uff25") || string.contains("\uff26") || string.contains("\uff27") || string.contains("\uff28") || string.contains("\uff29") || string.contains("\uff2a") || string.contains("\uff2b") || string.contains("\uff2c") || string.contains("\uff2d") || string.contains("\uff2e") || string.contains("\uff2f") || string.contains("\uff30") || string.contains("\uff32") || string.contains("\uff33") || string.contains("\uff34") || string.contains("\uff35") || string.contains("\uff37") || string.contains("\uff38") || string.contains("\uff39") || string.contains("\uff3a") || string.contains("\u24d0") || string.contains("\u24b6") || string.contains("\u24b7") || string.contains("\u24d1") || string.contains("\u24d2") || string.contains("\u24b8") || string.contains("\u24d3") || string.contains("\u24b9") || string.contains("\u24d4") || string.contains("\u24ba") || string.contains("\u24d5") || string.contains("\u24bb") || string.contains("\u24d6") || string.contains("\u24bc") || string.contains("\u24bd") || string.contains("\u24d7") || string.contains("\u24be") || string.contains("\u24d8") || string.contains("\u24bf") || string.contains("\u24d9") || string.contains("\u24c0") || string.contains("\u24da") || string.contains("\u24c1") || string.contains("\u24db") || string.contains("\u24c2") || string.contains("\u24dc") || string.contains("\u24c3") || string.contains("\u24dd") || string.contains("\u24c4") || string.contains("\u24de") || string.contains("\u24c5") || string.contains("\u24df") || string.contains("\u24c7") || string.contains("\u24e1") || string.contains("\u24c8") || string.contains("\u24e2") || string.contains("\u24c9") || string.contains("\u24e3") || string.contains("\u24ca") || string.contains("\u24e4") || string.contains("\u24cc") || string.contains("\u24e6") || string.contains("\u24cd") || string.contains("\u24e7") || string.contains("\u24ce") || string.contains("\u24e8") || string.contains("\u24cf") || string.contains("\u24e9")) {
                matchedFont = true;
            }
        }
        return matchedFont;
    }
}
