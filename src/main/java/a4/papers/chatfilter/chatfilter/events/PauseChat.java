package a4.papers.chatfilter.chatfilter.events;


import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

public class PauseChat implements EventExecutor, Listener {
    ChatFilter chatFilter;

    public PauseChat(ChatFilter instance) {
        chatFilter = instance;
    }
    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onPlayerChatPause((AsyncPlayerChatEvent) event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatPause(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;
        if (chatFilter.chatPause) {
            if (event.getPlayer().hasPermission("chatfilter.bypass") || event.getPlayer().hasPermission("chatfilter.pause") || event.getPlayer().hasPermission("chatfilter.bypass.pause")) {
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.denyMessagePause.s)));
                chatFilter.logMsg("[Chat filter] (Paused chat) " + event.getPlayer().getDisplayName() + ": " + event.getMessage());
            }

        }

    }

}
