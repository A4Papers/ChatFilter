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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatDelayListener implements EventExecutor, Listener {
    public Map<UUID, ChatData> chatmsgs = new HashMap<UUID, ChatData>();
    ChatFilter chatFilter;
    private HashMap<UUID, String> chatMSG = new HashMap<>();
    private HashMap<UUID, Long> cooldown = new HashMap<>();

    public ChatDelayListener(ChatFilter instance) {
        chatFilter = instance;
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onPlayerSpam((AsyncPlayerChatEvent) event);
    }

    @EventHandler
    public void onPlayerSpam(AsyncPlayerChatEvent e) {
        if (e.getPlayer().hasPermission("chatfilter.bypass") || e.getPlayer().hasPermission("chatfilter.bypass.repeat"))
            return;
        Player p = e.getPlayer();
        UUID playerUUID = p.getUniqueId();
        String msg = e.getMessage();
        chatmsgs.containsKey(playerUUID);
        long configtime = chatFilter.repeatDelay * 1000L;
        if (!p.hasPermission("chatfilter.bypass") || !e.getPlayer().hasPermission("chatfilter.bypass.repeat") && chatFilter.antiRepeatEnabled) {
            if (chatmsgs.containsKey(playerUUID)) {
                long time = chatmsgs.get(playerUUID).getLong();
                double sim = StringSimilarity.similarity(msg, chatmsgs.get(playerUUID).getString());
                BigDecimal d = new BigDecimal(chatFilter.percentage.trim().replace("%", "")).divide(BigDecimal.valueOf(100));
                if (sim > d.doubleValue()) {
                    if (time > System.currentTimeMillis()) {
                        e.setCancelled(true);
                        int remainingTime = Math.round((this.chatmsgs.get(playerUUID).getLong() - System.currentTimeMillis()) / 1000 * 10) / 10;
                        String timeString = "";
                        if (remainingTime >= 2) {
                            timeString = remainingTime + " seconds";
                        } else {
                            timeString = 1 + " second";
                        }
                        p.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.chatRepeatMessage.s).replace("%time%", timeString)));
                    } else {
                        chatmsgs.put(playerUUID, new ChatData(msg, System.currentTimeMillis() + configtime));
                    }
                } else {
                    chatmsgs.remove(playerUUID);
                }
            } else {
                chatmsgs.put(playerUUID, new ChatData(msg, System.currentTimeMillis() + configtime));
            }
        }
    }
}
