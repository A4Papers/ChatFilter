package a4.papers.chatfilter.chatfilter;

import a4.papers.chatfilter.chatfilter.scheduling.PluginScheduler;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

final class SchedulerFixture implements AutoCloseable {
    final Plugin plugin = mock(Plugin.class);
    final Server server = mock(Server.class);
    final GlobalRegionScheduler global = mock(GlobalRegionScheduler.class);
    final AsyncScheduler async = mock(AsyncScheduler.class);
    final EntityScheduler entity = mock(EntityScheduler.class);
    final Deque<QueuedTask> globalQueue = new ArrayDeque<>();
    final Deque<QueuedTask> asyncQueue = new ArrayDeque<>();
    final Deque<QueuedTask> entityQueue = new ArrayDeque<>();
    final PluginScheduler scheduler;

    SchedulerFixture() {
        when(plugin.getServer()).thenReturn(server);
        when(plugin.isEnabled()).thenReturn(true);
        when(plugin.getLogger()).thenReturn(mock(Logger.class));
        when(server.getGlobalRegionScheduler()).thenReturn(global);
        when(server.getAsyncScheduler()).thenReturn(async);
        when(global.run(eq(plugin), any())).thenAnswer(call -> enqueue(globalQueue, call.getArgument(1), null));
        when(global.runDelayed(eq(plugin), any(), anyLong())).thenAnswer(call -> enqueue(globalQueue, call.getArgument(1), null));
        when(global.runAtFixedRate(eq(plugin), any(), anyLong(), anyLong())).thenAnswer(call -> enqueue(globalQueue, call.getArgument(1), null));
        when(async.runNow(eq(plugin), any())).thenAnswer(call -> enqueue(asyncQueue, call.getArgument(1), null));
        when(entity.run(eq(plugin), any(), any())).thenAnswer(call -> enqueue(entityQueue, call.getArgument(1), call.getArgument(2)));
        scheduler = new PluginScheduler(plugin);
    }

    private QueuedTask enqueue(Deque<QueuedTask> queue, Consumer<ScheduledTask> callback, Runnable retired) {
        QueuedTask task = new QueuedTask(callback, retired);
        queue.add(task);
        return task;
    }

    @Override
    public void close() {
        scheduler.close();
    }

    final class QueuedTask implements ScheduledTask {
        final Consumer<ScheduledTask> callback;
        final Runnable retired;
        int cancellations;
        ExecutionState state = ExecutionState.IDLE;

        QueuedTask(Consumer<ScheduledTask> callback, Runnable retired) {
            this.callback = callback;
            this.retired = retired;
        }

        void run() {
            state = ExecutionState.RUNNING;
            try {
                callback.accept(this);
            } finally {
                state = ExecutionState.FINISHED;
            }
        }

        void retire() {
            state = ExecutionState.CANCELLED;
            retired.run();
        }

        @Override
        public Plugin getOwningPlugin() {
            return plugin;
        }

        @Override
        public boolean isRepeatingTask() {
            return false;
        }

        @Override
        public CancelledState cancel() {
            cancellations++;
            state = ExecutionState.CANCELLED;
            return CancelledState.CANCELLED_BY_CALLER;
        }

        @Override
        public ExecutionState getExecutionState() {
            return state;
        }
    }
}
