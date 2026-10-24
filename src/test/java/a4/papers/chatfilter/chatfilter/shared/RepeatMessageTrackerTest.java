package a4.papers.chatfilter.chatfilter.shared;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

class RepeatMessageTrackerTest {
    @Test
    void preservesExpiryAndSupportsThresholdChanges() {
        RepeatMessageTracker tracker = new RepeatMessageTracker();
        UUID player = UUID.randomUUID();
        tracker.join(player);
        assertEquals(0, tracker.record(player, "hello", 1000, 5000, 0.6));
        assertEquals(4500, tracker.record(player, "hello", 1500, 5000, 0.6));
        assertEquals(4000, tracker.record(player, "hella", 2000, 5000, 0.6));
        assertEquals(0, tracker.record(player, "hella", 2000, 5000, 0.9));
        assertEquals(0, tracker.record(player, "hella", 7000, 5000, 0.6));
        assertEquals(4000, tracker.record(player, "hella", 8000, 5000, 0.6));
    }

    @Test
    void disconnectAndReconnectDiscardOldState() {
        RepeatMessageTracker tracker = new RepeatMessageTracker();
        UUID player = UUID.randomUUID();
        tracker.join(player);
        tracker.record(player, "hello", 1000, 5000, 0.6);
        tracker.quit(player);
        assertEquals(0, tracker.record(player, "hello", 1100, 5000, 0.6));
        tracker.join(player);
        assertEquals(0, tracker.record(player, "hello", 1200, 5000, 0.6));
        tracker.clear();
        assertEquals(0, tracker.record(player, "hello", 1300, 5000, 0.6));
    }

    @Test
    void concurrentMessagesForOnePlayerAreAtomic() throws Exception {
        RepeatMessageTracker tracker = new RepeatMessageTracker();
        UUID player = UUID.randomUUID();
        tracker.join(player);
        try (var executor = Executors.newFixedThreadPool(8)) {
            List<Callable<Long>> calls = new ArrayList<>();
            for (int i = 0; i < 100; i++) calls.add(() -> tracker.record(player, "hello", 1000, 5000, 0.6));
            int allowed = 0;
            for (var result : executor.invokeAll(calls)) if (result.get() == 0) allowed++;
            assertEquals(1, allowed);
        }
    }
}
