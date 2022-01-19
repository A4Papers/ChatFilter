package a4.papers.chatfilter.chatfilter.events;


import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.lang.ChatData;
import a4.papers.chatfilter.chatfilter.lang.EnumStrings;
import a4.papers.chatfilter.chatfilter.lang.StringSimilarity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatDelayListener implements Listener {
    public Map<UUID, ChatData> chatmsgs = new HashMap<UUID, ChatData>();
    ChatFilter chatFilter;
    int taskID;
    Map<Player, Integer> tasks = new HashMap<Player, Integer>();
    private HashMap<UUID, String> chatMSG = new HashMap<>();
    private HashMap<UUID, Long> cooldown = new HashMap<>();

    public ChatDelayListener(ChatFilter instance) {
        chatFilter = instance;
    }

    @EventHandler
    public void onPlayerSpam(AsyncPlayerChatEvent e) {
        if (e.getPlayer().hasPermission("chatfilter.bypass") || e.getPlayer().hasPermission("chatfilter.bypass.repeat"))
            return;
        Player p = e.getPlayer();
        UUID playerUUID = p.getUniqueId();
        String msg = e.getMessage();
        chatmsgs.containsKey(playerUUID);
        long configtime = (chatFilter.getConfig().getInt("settings.repeatDelay") * 1000L);
        if (!p.hasPermission("chatfilter.bypass") || e.getPlayer().hasPermission("chatfilter.bypass.repeat") && chatFilter.antiRepeatEnabled) {
            if (chatmsgs.containsKey(playerUUID)) {
                long time = chatmsgs.get(playerUUID).getLong();
                double sim = StringSimilarity.similarity(msg, chatmsgs.get(playerUUID).getString());
                BigDecimal d = new BigDecimal(chatFilter.percentage.trim().replace("%", "")).divide(BigDecimal.valueOf(100));
                if (sim > d.doubleValue()) {
                    if (time > System.currentTimeMillis()) {
                        e.setCancelled(true);
                        p.sendMessage(chatFilter.colour(chatFilter.mapToString(EnumStrings.chatRepeatMessage.s)));
                    } else {
                        chatmsgs.put(playerUUID, new ChatData(msg, System.currentTimeMillis() + configtime));
                    }
                } else {
                    chatmsgs.remove(playerUUID);
                }
            }
            if (!cooldown.containsKey(playerUUID)) {
                chatmsgs.put(playerUUID, new ChatData(msg, System.currentTimeMillis() + configtime));
            }
        }
    }
}
