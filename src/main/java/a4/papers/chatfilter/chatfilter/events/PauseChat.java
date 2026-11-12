package a4.papers.chatfilter.chatfilter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import io.papermc.paper.event.player.AsyncChatEvent;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;

public class PauseChat implements EventExecutor, Listener {
    ChatFilter chatFilter;

    public PauseChat(ChatFilter instance) {
        chatFilter = instance;
    }
    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onPlayerChatPause((AsyncChatEvent) event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatPause(AsyncChatEvent event) {
        if (event.isCancelled() || !chatFilter.chatPause)
            return;
        
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("chatfilter.bypass") || player.hasPermission("chatfilter.pause") || player.hasPermission("chatfilter.bypass.pause")) {
            return;
        }
        
        event.setCancelled(true);
        player.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.denyMessagePause.s)));
        chatFilter.logMsg("[Chat filter] (Paused chat) " + player.getDisplayName() + ": " + chatFilter.plainMessage(event));
    }

}
