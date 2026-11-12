package a4.papers.chatfilter.chatfilter.events;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import io.papermc.paper.event.player.AsyncChatEvent;

import a4.papers.chatfilter.chatfilter.ChatFilter;

public class RepeatCharListener implements EventExecutor, Listener {

    ChatFilter chatFilter;

    public RepeatCharListener(ChatFilter instance) {
        this.chatFilter = instance;
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onPlayerCarSpam((AsyncChatEvent) event);
    }

    public void onPlayerCarSpam(AsyncChatEvent event) {
        if (!chatFilter.antiSpamEnabled) {
            return;
        }
        org.bukkit.entity.Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("chatfilter.bypass") || player.hasPermission("chatfilter.bypass.characters")) {
            return;
        }
        String msg = chatFilter.plainMessage(event);
        java.util.regex.Matcher matcher = chatFilter.antiSpamPattern.matcher(msg);
        if (!matcher.find()) {
            return;
        }
        if (chatFilter.urlPattern.matcher(msg).find()) {
            return;
        }
        chatFilter.setPlainMessage(event, matcher.replaceAll(chatFilter.getAntiSpamReplacementToken()));
    }
}
