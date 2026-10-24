package a4.papers.chatfilter.chatfilter.events;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import io.papermc.paper.event.player.AsyncChatEvent;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.PlayerNameIndex;

public class CapsChatListener implements EventExecutor, Listener {

    ChatFilter chatFilter;
    private final PlayerNameIndex playerNames = new PlayerNameIndex();

    public CapsChatListener(ChatFilter instance) {
        this.chatFilter = instance;
    }

    public void track(Player player) {
        playerNames.join(player.getUniqueId(), player.getName());
    }

    public void untrack(UUID player) {
        playerNames.quit(player);
    }

    public void clear() {
        playerNames.clear();
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onPlayerCaps((AsyncChatEvent) event);
    }

    public void onPlayerCaps(AsyncChatEvent event) {
        if (!chatFilter.deCap) {
            return;
        }
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("chatfilter.bypass") || player.hasPermission("chatfilter.bypass.caps"))
            return;
        String msg = chatFilter.plainMessage(event);
        if (msg.isEmpty()) {
            return;
        }
        if (chatFilter.urlPattern.matcher(msg).find())
            return;
        int length = msg.length();
        int counter = 0;
        for (int i = 0; i < length; i++) {
            if (Character.isUpperCase(msg.charAt(i)) && ++counter >= chatFilter.capsAmount) {
                break;
            }
        }
        if (counter >= chatFilter.capsAmount) {
            msg = msg.charAt(0) + msg.substring(1).toLowerCase();
        }
        String newmsg = playerNames.restore(msg);
        if (!newmsg.equals(msg)) {
            chatFilter.setPlainMessage(event, newmsg);
        }
    }
}
