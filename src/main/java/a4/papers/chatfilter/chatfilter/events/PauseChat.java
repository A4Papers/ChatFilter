package a4.papers.chatfilter.chatfilter.events;


import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.lang.enumStrings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PauseChat implements Listener {
    ChatFilter chatFilter;

    public PauseChat(ChatFilter instance) {
        chatFilter = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatPause(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (chatFilter.chatPause) {
            if (event.getPlayer().hasPermission("chatfilter.bypass") || event.getPlayer().hasPermission("chatfilter.pause") || event.getPlayer().hasPermission("chatfilter.bypass.pause")) {

            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(chatFilter.colour(chatFilter.mapToString(enumStrings.denyMessagePause.s)));
                chatFilter.logMsg("[Chat filter] (Paused chat) " + event.getPlayer().getDisplayName() + ": " + event.getMessage());
            }

        }

    }

}
