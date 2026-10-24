package a4.papers.chatfilter.chatfilter;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageCacheTest {
    @Test
    void observesComponentChangesMadeByOtherListeners() {
        ChatFilter plugin = mock(ChatFilter.class);
        doCallRealMethod().when(plugin).plainMessage(any());
        doCallRealMethod().when(plugin).setPlainMessage(any(), anyString());
        AsyncChatEvent event = new AsyncChatEvent(true, mock(Player.class), Set.of(),
                (player, name, message, viewer) -> message, Component.text("first"), Component.text("first"), null);
        assertEquals("first", plugin.plainMessage(event));
        event.message(Component.text("second"));
        assertEquals("second", plugin.plainMessage(event));
        plugin.setPlainMessage(event, "third");
        assertEquals("third", plugin.plainMessage(event));
        event.message(Component.text("fourth"));
        assertEquals("fourth", plugin.plainMessage(event));
    }
}
