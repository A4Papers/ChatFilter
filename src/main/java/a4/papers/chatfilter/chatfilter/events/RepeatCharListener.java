package a4.papers.chatfilter.chatfilter.events;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepeatCharListener implements EventExecutor, Listener {

    ChatFilter chatFilter;

    public RepeatCharListener(ChatFilter instance) {
        this.chatFilter = instance;
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onPlayerCarSpam((AsyncPlayerChatEvent) event);
    }

    public void onPlayerCarSpam(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        if (chatFilter.antiSpamEnabled) {
            if (event.getPlayer().hasPermission("chatfilter.bypass") || event.getPlayer().hasPermission("chatfilter.bypass.characters")) {
                return;
            }
            if (isURL(msg)) {
                return;
            }
            event.setMessage(chatFilter.antiSpamPattern.matcher(msg).replaceAll(new String(new char[chatFilter.antiSpamReplaceAmount]).replace("\0", "$1")));
        }
    }

    public boolean isURL(String str) {
        boolean matched = false;
        Pattern p = Pattern.compile(chatFilter.URL_REGEX);
        Matcher m = p.matcher(str);
        if (m.find()) {
            matched = true;
        }
        return matched;
    }
}
