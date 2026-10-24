package a4.papers.chatfilter.chatfilter.events;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.RepeatMessageTracker;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.EventExecutor;
import io.papermc.paper.event.player.AsyncChatEvent;

public class ChatDelayListener implements EventExecutor, Listener {
    private final RepeatMessageTracker tracker = new RepeatMessageTracker();
    ChatFilter chatFilter;

    public ChatDelayListener(ChatFilter instance) {
        chatFilter = instance;
    }

    public void track(Player player) {
        tracker.join(player.getUniqueId());
    }

    public void onJoin(PlayerJoinEvent event) {
        track(event.getPlayer());
    }

    public void onQuit(PlayerQuitEvent event) {
        tracker.quit(event.getPlayer().getUniqueId());
    }

    public void clear() {
        tracker.clear();
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onPlayerSpam((AsyncChatEvent) event);
    }

    public void onPlayerSpam(AsyncChatEvent e) {
        if (!chatFilter.antiRepeatEnabled) {
            return;
        }

        Player p = e.getPlayer();
        if (p.isOp() || p.hasPermission("chatfilter.bypass") || p.hasPermission("chatfilter.bypass.repeat")) {
            return;
        }

        String msg = chatFilter.plainMessage(e).toLowerCase();
        long currentTime = System.currentTimeMillis();
        long configtime = chatFilter.repeatDelay * 1000L;
        long remainingMs = tracker.record(p.getUniqueId(), msg, currentTime, configtime, chatFilter.repeatSimilarityThreshold);
        if (remainingMs > 0) {
            e.setCancelled(true);
            int remainingTime = (int) Math.ceil(remainingMs / 1000.0);
            String timeString = remainingTime >= 2 ? remainingTime + " seconds" : "1 second";
            p.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.chatRepeatMessage.s).replace("%time%", timeString)));
        }
    }
}
