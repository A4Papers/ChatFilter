package a4.papers.chatfilter.chatfilter.scheduling;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.command.CommandSender;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.List;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PluginScheduler implements AutoCloseable {
    private final Plugin plugin;
    private final Server server;
    private final GlobalRegionScheduler global;
    private final AsyncScheduler async;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Set<EntityWork> entityTasks = ConcurrentHashMap.newKeySet();

    public PluginScheduler(Plugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
        global = server.getGlobalRegionScheduler();
        async = server.getAsyncScheduler();
    }

    public ScheduledTask global(Runnable action) {
        return schedule(() -> global.run(plugin, guard(action)));
    }

    public ScheduledTask globalLater(Runnable action, long delayTicks) {
        if (delayTicks <= 0) {
            return global(action);
        }
        return schedule(() -> global.runDelayed(plugin, guard(action), delayTicks));
    }

    public ScheduledTask globalTimer(Runnable action, long delayTicks, long periodTicks) {
        if (periodTicks <= 0) {
            throw new IllegalArgumentException("Period must be positive");
        }
        return schedule(() -> global.runAtFixedRate(plugin, guard(action), Math.max(1, delayTicks), periodTicks));
    }

    public ScheduledTask async(Runnable action) {
        return schedule(() -> async.runNow(plugin, guard(action)));
    }

    public ScheduledTask entity(Entity entity, Runnable action) {
        if (closed.get()) {
            return null;
        }
        EntityWork work = new EntityWork(action);
        entityTasks.add(work);
        try {
            ScheduledTask task = schedule(() -> entity.getScheduler().run(plugin, work, work::retire));
            work.attach(task);
            return task == null ? null : work;
        } catch (RuntimeException error) {
            work.retire();
            throw error;
        }
    }

    public ScheduledTask sender(CommandSender sender, Runnable action) {
        return sender instanceof Entity entity ? entity(entity, action) : global(action);
    }

    public ScheduledTask commands(List<String> commands) {
        if (commands.isEmpty()) {
            return null;
        }
        List<String> pending = List.copyOf(commands);
        return global(() -> {
            for (String command : pending) {
                try {
                    server.dispatchCommand(server.getConsoleSender(), command);
                } catch (RuntimeException error) {
                    plugin.getLogger().log(Level.WARNING, "Could not execute filter command: " + command, error);
                }
            }
        });
    }

    private Consumer<ScheduledTask> guard(Runnable action) {
        return task -> {
            if (!closed.get()) {
                action.run();
            }
        };
    }

    private ScheduledTask schedule(Supplier<ScheduledTask> registration) {
        if (closed.get()) {
            return null;
        }
        try {
            ScheduledTask task = registration.get();
            if (closed.get() && task != null) {
                task.cancel();
            }
            return task;
        } catch (IllegalPluginAccessException error) {
            if (closed.get() || !plugin.isEnabled()) {
                return null;
            }
            throw error;
        }
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        try {
            global.cancelTasks(plugin);
        } finally {
            try {
                async.cancelTasks(plugin);
            } finally {
                for (EntityWork work : entityTasks) {
                    work.cancel();
                }
                entityTasks.clear();
            }
        }
    }

    private final class EntityWork implements Consumer<ScheduledTask>, ScheduledTask {
        private volatile Runnable action;
        private volatile ScheduledTask task;
        private volatile boolean finished;

        private EntityWork(Runnable action) {
            this.action = action;
        }

        private void attach(ScheduledTask task) {
            this.task = task;
            if (task == null) {
                retire();
            } else if (finished || closed.get()) {
                task.cancel();
                retire();
            }
        }

        @Override
        public void accept(ScheduledTask task) {
            try {
                Runnable current = action;
                if (!closed.get() && current != null) {
                    current.run();
                }
            } finally {
                retire();
            }
        }

        private void retire() {
            finished = true;
            action = null;
            entityTasks.remove(this);
        }

        @Override
        public CancelledState cancel() {
            retire();
            ScheduledTask current = task;
            return current == null ? CancelledState.CANCELLED_BY_CALLER : current.cancel();
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
        public ExecutionState getExecutionState() {
            return task.getExecutionState();
        }
    }
}
