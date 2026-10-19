package a4.papers.chatfilter.chatfilter.events;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.ChatData;
import a4.papers.chatfilter.chatfilter.shared.StringSimilarity;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import io.papermc.paper.event.player.AsyncChatEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatDelayListener implements EventExecutor, Listener {
    private static final int CLEANUP_INTERVAL = 256;
    static {
        if ((CLEANUP_INTERVAL & (CLEANUP_INTERVAL - 1)) != 0) {
            throw new IllegalStateException("CLEANUP_INTERVAL must be a power of 2");
        }
    }
    public final Map<UUID, ChatData> chatmsgs = new ConcurrentHashMap<>();
    ChatFilter chatFilter;
    private volatile double similarityThreshold = -1.0;
    private int cleanupCounter;

    public ChatDelayListener(ChatFilter instance) {
        chatFilter = instance;
    }

    private double getSimilarityThreshold() {
        double threshold = similarityThreshold;
        if (threshold < 0.0) {
            String percent = chatFilter.percentage.trim().replace("%", "");
            threshold = Double.parseDouble(percent) / 100.0;
            similarityThreshold = threshold;
        }
        return threshold;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        chatmsgs.remove(event.getPlayer().getUniqueId());
    }

    private void pruneExpiredEntries(long now) {
        Iterator<Map.Entry<UUID, ChatData>> iterator = chatmsgs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ChatData> entry = iterator.next();
            if (entry.getValue().getLong() <= now) {
                iterator.remove();
            }
        }
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onPlayerSpam((AsyncChatEvent) event);
    }

    @EventHandler
    public void onPlayerSpam(AsyncChatEvent e) {
        if (!chatFilter.antiRepeatEnabled) {
            return;
        }

        Player p = e.getPlayer();
        if (p.isOp() || p.hasPermission("chatfilter.bypass") || p.hasPermission("chatfilter.bypass.repeat")) {
            return;
        }

        UUID playerUUID = p.getUniqueId();
        String msg = chatFilter.plainMessage(e).toLowerCase();
        long currentTime = System.currentTimeMillis();
        long configtime = chatFilter.repeatDelay * 1000L;

        if ((++cleanupCounter & (CLEANUP_INTERVAL - 1)) == 0) {
            pruneExpiredEntries(currentTime);
        }

        ChatData chatData = chatmsgs.get(playerUUID);
        if (chatData == null) {
            chatmsgs.put(playerUUID, new ChatData(msg, currentTime + configtime));
            return;
        }

        long expiryTime = chatData.getLong();
        double sim = StringSimilarity.similarity(msg, chatData.getString());

        if (sim > getSimilarityThreshold()) {
            if (expiryTime > currentTime) {
                e.setCancelled(true);
                long remainingMs = expiryTime - currentTime;
                int remainingTime = (int) Math.ceil(remainingMs / 1000.0);
                String timeString = remainingTime >= 2 ? remainingTime + " seconds" : "1 second";
                p.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.chatRepeatMessage.s).replace("%time%", timeString)));
            } else {
                chatmsgs.put(playerUUID, new ChatData(msg, currentTime + configtime));
            }
        } else {
            chatmsgs.put(playerUUID, new ChatData(msg, currentTime + configtime));
        }
    }
}
