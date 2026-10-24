package a4.papers.chatfilter.chatfilter.shared;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PlayerNameIndexTest {
    @Test
    void matchesOrderedNameRestorationIncludingOverlaps() {
        PlayerNameIndex index = new PlayerNameIndex();
        List<String> names = List.of("Ann", "Anna", "JOE", "xX_Player_Xx");
        for (String name : names) index.join(UUID.randomUUID(), name);
        for (String message : List.of("hello everyone", "anna and joe", "ANN and Joe", "xX_player_xx", "annanna")) {
            String expected = message;
            for (String name : names) {
                if (message.toLowerCase().contains(name.toLowerCase())) expected = expected.replace(name.toLowerCase(), name);
            }
            assertEquals(expected, index.restore(message));
        }
    }

    @Test
    void updatesOnPlayerLifecycleChanges() {
        PlayerNameIndex index = new PlayerNameIndex();
        UUID player = UUID.randomUUID();
        index.join(player, "Player");
        assertEquals("hello Player", index.restore("hello player"));
        index.quit(player);
        assertEquals("hello player", index.restore("hello player"));
        index.join(player, "Player");
        index.clear();
        assertEquals("hello player", index.restore("hello player"));
    }
}
