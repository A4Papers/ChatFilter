package a4.papers.chatfilter.chatfilter.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import io.papermc.paper.event.player.AsyncChatEvent;

import a4.papers.chatfilter.chatfilter.ChatFilter;

public class CapsChatListener implements EventExecutor, Listener {

    ChatFilter chatFilter;

    public CapsChatListener(ChatFilter instance) {
        this.chatFilter = instance;
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onPlayerCaps((AsyncChatEvent) event);
    }

    public void onPlayerCaps(AsyncChatEvent event) {
        if (!chatFilter.deCap) {
            return;
        }
        String msg = chatFilter.plainMessage(event);
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("chatfilter.bypass") || player.hasPermission("chatfilter.bypass.caps"))
            return;
        if (chatFilter.urlPattern.matcher(msg).find())
            return;
        int length = msg.length();
        int counter = 0;
        for (int i = 0; i < length; i++) {
            if (Character.isUpperCase(msg.charAt(i))) counter++;
        }
        if (counter >= chatFilter.capsAmount) {
            msg = msg.charAt(0) + msg.substring(1).toLowerCase();
        }
        String msgLower = msg.toLowerCase();
        String newmsg = msg;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String playerName = onlinePlayer.getName();
            String playerNameLower = playerName.toLowerCase();
            if (msgLower.contains(playerNameLower)) {
                newmsg = newmsg.replace(playerNameLower, playerName);
            }
        }
        if (!newmsg.equals(msg)) {
            chatFilter.setPlainMessage(event, newmsg);
        }
    }
}
