package a4.papers.chatfilter.chatfilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Manager {

    ChatFilter chatFilter;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#" + "([A-Fa-f0-9]{6})");
    private final boolean hexSupported;
    private final boolean textComponentSupported;

    public Manager(ChatFilter instance) {
        chatFilter = instance;
        int version = parseVersion();
        hexSupported = version >= 16;
        textComponentSupported = version >= 12;
    }

    private static int parseVersion() {
        try {
            return Integer.parseInt(Bukkit.getBukkitVersion().split("[.\\-]")[1]);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    public static final char COLOR_CHAR = '\u00A7';

    public boolean supported(String string) {
        switch (string.toLowerCase()) {
            case "hex":
                return hexSupported;
            case "text-component":
                return textComponentSupported;
            default:
                return false;
        }
    }

    public static String colorStringHex(String msg) {
        Matcher matcher = HEX_PATTERN.matcher(msg);
        if (!matcher.find()) {
            return ChatColor.translateAlternateColorCodes('&', msg);
        }
        StringBuilder buffer = new StringBuilder(msg.length() + 4 * 8);
        int lastEnd = 0;
        do {
            buffer.append(msg, lastEnd, matcher.start());
            String group = matcher.group(1);
            buffer.append(COLOR_CHAR).append("x")
                    .append(COLOR_CHAR).append(group.charAt(0)).append(COLOR_CHAR).append(group.charAt(1))
                    .append(COLOR_CHAR).append(group.charAt(2)).append(COLOR_CHAR).append(group.charAt(3))
                    .append(COLOR_CHAR).append(group.charAt(4)).append(COLOR_CHAR).append(group.charAt(5));
            lastEnd = matcher.end();
        } while (matcher.find());
        buffer.append(msg, lastEnd, msg.length());
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
