package a4.papers.chatfilter.chatfilter;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PluginSchedulerTest {
    @Test
    void routesGlobalAndAsyncWorkToNativeSchedulers() {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            AtomicInteger calls = new AtomicInteger();
            ScheduledTask global = fixture.scheduler.global(calls::incrementAndGet);
            ScheduledTask async = fixture.scheduler.async(calls::incrementAndGet);
            assertSame(fixture.globalQueue.peek(), global);
            assertSame(fixture.asyncQueue.peek(), async);
            assertEquals(0, calls.get());
            fixture.globalQueue.remove().run();
            fixture.asyncQueue.remove().run();
            assertEquals(2, calls.get());
            verify(fixture.server, never()).getScheduler();
        }
    }

    @Test
    void usesTicksForDelayedAndRepeatingGlobalWork() {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            Runnable action = () -> {};
            fixture.scheduler.globalLater(action, 40);
            fixture.scheduler.globalLater(action, 0);
            fixture.scheduler.globalTimer(action, 0, 20);
            verify(fixture.global).runDelayed(eq(fixture.plugin), any(), eq(40L));
            verify(fixture.global).run(eq(fixture.plugin), any());
            verify(fixture.global).runAtFixedRate(eq(fixture.plugin), any(), eq(1L), eq(20L));
            assertThrows(IllegalArgumentException.class, () -> fixture.scheduler.globalTimer(action, 1, 0));
        }
    }

    @Test
    void senderWorkFollowsPlayersAndConsoleUsesGlobalScheduler() {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            Player player = mock(Player.class);
            when(player.getScheduler()).thenReturn(fixture.entity);
            AtomicInteger calls = new AtomicInteger();
            ScheduledTask handle = fixture.scheduler.sender(player, calls::incrementAndGet);
            fixture.scheduler.sender(mock(ConsoleCommandSender.class), calls::incrementAndGet);
            assertEquals(1, fixture.entityQueue.size());
            assertEquals(1, fixture.globalQueue.size());
            assertSame(fixture.plugin, handle.getOwningPlugin());
            assertFalse(handle.isRepeatingTask());
            assertEquals(ScheduledTask.ExecutionState.IDLE, handle.getExecutionState());
            fixture.entityQueue.remove().run();
            fixture.globalQueue.remove().run();
            assertEquals(2, calls.get());
        }
    }

    @Test
    void closeCancelsPendingEntityWorkAndSuppressesLateCallbacks() {
        SchedulerFixture fixture = new SchedulerFixture();
        Player player = mock(Player.class);
        when(player.getScheduler()).thenReturn(fixture.entity);
        AtomicInteger calls = new AtomicInteger();
        fixture.scheduler.global(calls::incrementAndGet);
        fixture.scheduler.async(calls::incrementAndGet);
        fixture.scheduler.entity(player, calls::incrementAndGet);
        var pending = fixture.entityQueue.peek();
        fixture.close();
        fixture.close();
        assertEquals(1, pending.cancellations);
        fixture.entityQueue.remove().run();
        fixture.globalQueue.remove().run();
        fixture.asyncQueue.remove().run();
        assertEquals(0, calls.get());
        assertNull(fixture.scheduler.global(calls::incrementAndGet));
        assertNull(fixture.scheduler.async(calls::incrementAndGet));
        assertNull(fixture.scheduler.entity(player, calls::incrementAndGet));
        verify(fixture.global, times(1)).cancelTasks(fixture.plugin);
        verify(fixture.async, times(1)).cancelTasks(fixture.plugin);
    }

    @Test
    void releasesCompletedRetiredAndManuallyCancelledEntityWork() {
        SchedulerFixture fixture = new SchedulerFixture();
        Player player = mock(Player.class);
        when(player.getScheduler()).thenReturn(fixture.entity);
        fixture.scheduler.entity(player, () -> {});
        var completed = fixture.entityQueue.remove();
        completed.run();
        fixture.scheduler.entity(player, () -> fail("Retired action ran"));
        var retired = fixture.entityQueue.remove();
        retired.retire();
        ScheduledTask handle = fixture.scheduler.entity(player, () -> fail("Cancelled action ran"));
        var cancelled = fixture.entityQueue.remove();
        handle.cancel();
        cancelled.run();
        fixture.close();
        assertEquals(0, completed.cancellations);
        assertEquals(0, retired.cancellations);
        assertEquals(1, cancelled.cancellations);
    }

    @Test
    void handlesAlreadyRetiredEntitiesWithoutQueuingPlayerWork() {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            Player player = mock(Player.class);
            when(player.getScheduler()).thenReturn(fixture.entity);
            when(fixture.entity.run(any(), any(), any())).thenReturn(null);
            assertNull(fixture.scheduler.entity(player, () -> fail("Retired entity action ran")));
            assertTrue(fixture.entityQueue.isEmpty());
        }
    }

    @Test
    void cancelsTasksRegisteredDuringShutdown() {
        SchedulerFixture fixture = new SchedulerFixture();
        ScheduledTask task = mock(ScheduledTask.class);
        when(fixture.global.run(any(), any())).thenAnswer(call -> {
            fixture.close();
            return task;
        });
        fixture.scheduler.global(() -> fail("Shutdown action ran"));
        verify(task).cancel();
    }

    @Test
    void cancelsEntityTaskWhenShutdownPrecedesHandleAttachment() {
        SchedulerFixture fixture = new SchedulerFixture();
        Player player = mock(Player.class);
        when(player.getScheduler()).thenReturn(fixture.entity);
        ScheduledTask task = mock(ScheduledTask.class);
        when(fixture.entity.run(any(), any(), any())).thenAnswer(call -> {
            fixture.close();
            return task;
        });
        fixture.scheduler.entity(player, () -> fail("Shutdown entity action ran"));
        verify(task, atLeastOnce()).cancel();
    }

    @Test
    void toleratesPluginDisableRacesAndPropagatesOtherRegistrationFailures() {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            when(fixture.global.run(any(), any())).thenThrow(new IllegalPluginAccessException("disabled"));
            when(fixture.plugin.isEnabled()).thenReturn(false);
            assertNull(fixture.scheduler.global(() -> {}));
            when(fixture.plugin.isEnabled()).thenReturn(true);
            assertThrows(IllegalPluginAccessException.class, () -> fixture.scheduler.global(() -> {}));
        }
    }

    @Test
    void batchesCommandsInOrderAndContinuesAfterOneFails() {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            ConsoleCommandSender console = mock(ConsoleCommandSender.class);
            when(fixture.server.getConsoleSender()).thenReturn(console);
            when(fixture.server.dispatchCommand(console, "broken")).thenThrow(new IllegalStateException("test failure"));
            fixture.scheduler.commands(List.of("first", "broken", "last"));
            assertEquals(1, fixture.globalQueue.size());
            verify(fixture.server, never()).dispatchCommand(any(), anyString());
            fixture.globalQueue.remove().run();
            var order = inOrder(fixture.server);
            order.verify(fixture.server).dispatchCommand(console, "first");
            order.verify(fixture.server).dispatchCommand(console, "broken");
            order.verify(fixture.server).dispatchCommand(console, "last");
            assertNull(fixture.scheduler.commands(List.of()));
            assertTrue(fixture.globalQueue.isEmpty());
        }
    }
}
